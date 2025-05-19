from flask import Flask, request, jsonify
from flask_cors import CORS
import pickle
import os
from dotenv import load_dotenv
from database import save_prediction, get_training_data
from model_retraining import retrain_model

import logging

logging.basicConfig(
    level=logging.INFO,
    format='%(asctime)s [%(levelname)s] %(message)s',
    handlers=[logging.StreamHandler()]
)

model_path="models/regmodel.pkl"
scaler_path="models/scaler.pkl"

from database import init_db

@app.before_first_request
def initialize_database():
    logging.info("Initializing database...")
    init_db()

load_dotenv()

app = Flask(__name__)
CORS(app)

def load_model():    
    with open(model_path, 'rb') as f:
        model = pickle.load(f)
    with open(scaler_path, 'rb') as f:
        scaler = pickle.load(f)
    return model, scaler

@app.route('/predict', methods=['POST'])
def predict():
    try:
        required_fields = ['age', 'sex', 'cp', 'trtbps', 'chol', 'fbs', 'restecg',
                   'thalachh', 'exng', 'oldpeak', 'slp', 'caa', 'thall']

        data = request.get_json()

        missing_fields = [field for field in required_fields if field not in data]
        if missing_fields:
            return jsonify({'error': f'Missing fields: {", ".join(missing_fields)}'}), 400
        
        # Extract features in the correct order
        features = [
            data['age'], data['sex'], data['cp'],
            data['trtbps'], data['chol'], data['fbs'],
            data['restecg'], data['thalachh'], data['exng'],
            data['oldpeak'], data['slp'], data['caa'],
            data['thall']
        ]
        
        # Load model and scaler
        model, scaler = load_model()
        
        # Scale features
        features_scaled = scaler.transform([features])
        
        # Make prediction
        risk_score = float(model.predict_proba(features_scaled)[0][1])  # Get probability of class 1
        risk_level = 'HIGH' if risk_score > 0.5 else 'LOW'
        
        # Save prediction to database
        saved_prediction = save_prediction(data, risk_score, risk_level)
        
        logging.info("Prediction complete")
        return jsonify({
            'id': saved_prediction.id,
            'risk_score': risk_score,
            'risk_level': risk_level,
            'timestamp': saved_prediction.timestamp.isoformat()
        })
        
    except Exception as e:
        logging.error("Prediction failed", exc_info=True)
        return jsonify({'error': str(e)}), 400

@app.route('/retrain', methods=['POST'])
def retrain():
    try:
        success, model_path = retrain_model()
        if not success:
            return jsonify({'error': 'Model retraining failed'}), 500
            
        return jsonify({
            'message': 'Model retrained successfully',
            'model_path': model_path
        })
        
    except Exception as e:
        return jsonify({'error': str(e)}), 500

@app.route('/health', methods=['GET'])
def health_check():
    return jsonify({'status': 'healthy'})

if __name__ == '__main__':
    app.run(host='0.0.0.0', port=5000) 