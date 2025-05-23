from flask import Flask, request, jsonify
import pickle
import numpy as np
from flask_cors import CORS

app = Flask(__name__)
CORS(app)  # Enable CORS for all routes

# Load the model
with open('models/model.pkl', 'rb') as f:
    model = pickle.load(f)

@app.route('/predict', methods=['POST'])
def predict():
    try:
        data = request.json
        features = [
            data['age'],
            data['sex'],
            data['cp'],
            data['trtbps'],
            data['chol'],
            data['fbs'],
            data['restecg'],
            data['thalachh'],
            data['exng'],
            data['oldpeak'],
            data['slp'],
            data['caa'],
            data['thall']
        ]
        
        # Convert to numpy array and reshape
        features = np.array(features).reshape(1, -1)
        
        # Get prediction probabilities
        prediction_proba = model.predict_proba(features)[0]
        probability = prediction_proba[1]  # Probability of heart attack (0-1)
        
        # Determine risk level
        if probability < 0.3:
            risk_level = "LOW"
        elif probability < 0.6:
            risk_level = "MODERATE"
        elif probability < 0.8:
            risk_level = "HIGH"
        else:
            risk_level = "CRITICAL"
            
        # Generate diagnosis text based on risk level
        diagnosis_map = {
            "LOW": "Patient shows low risk of heart attack. Regular check-ups recommended.",
            "MODERATE": "Patient shows moderate risk of heart attack. Lifestyle changes and monitoring advised.",
            "HIGH": "Patient shows high risk of heart attack. Immediate medical attention and lifestyle changes required.",
            "CRITICAL": "Patient shows critical risk of heart attack. Urgent medical intervention needed."
        }
            
        return jsonify({
            'riskScore': probability,  # Send as decimal (0-1) as frontend expects
            'riskLevel': risk_level,
            'diagnosis': diagnosis_map[risk_level]
        })
        
    except Exception as e:
        return jsonify({'error': str(e)}), 500

if __name__ == '__main__':
    app.run(host='0.0.0.0', port=5000) 