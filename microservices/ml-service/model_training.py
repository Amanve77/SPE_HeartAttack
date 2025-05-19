import pandas as pd
import numpy as np
from sklearn.model_selection import train_test_split
from sklearn.metrics import accuracy_score, roc_auc_score
import pickle
import os
from dotenv import load_dotenv
import dvc.api
from sklearn.preprocessing import StandardScaler
from database import get_training_data

load_dotenv()

model_path="models/regmodel.pkl"
scaler_path="models/scaler.pkl"

def prepare_data_from_db():
    """Convert database records to training data"""
    predictions = get_training_data()
    data = []
    for pred in predictions:
        features = [
            pred.age, pred.sex, pred.cp, pred.trtbps, pred.chol,
            pred.fbs, pred.restecg, pred.thalachh, pred.exng,
            pred.oldpeak, pred.slp, pred.caa, pred.thall
        ]
        # Using risk_score > 0.5 as the target variable
        target = 1 if pred.risk_score > 0.5 else 0
        data.append(features + [target])
    
    if not data:
        return None, None
    
    df = pd.DataFrame(data, columns=[
        'age', 'sex', 'cp', 'trtbps', 'chol', 'fbs', 'restecg',
        'thalachh', 'exng', 'oldpeak', 'slp', 'caa', 'thall', 'target'
    ])
    
    X = df.drop('target', axis=1)
    y = df['target']
    return X, y

def evaluate_model(model, X_test, y_test):
    """Evaluate model performance"""
    y_pred = model.predict(X_test)
    accuracy = accuracy_score(y_test, y_pred)
    auc = roc_auc_score(y_test, model.predict_proba(X_test)[:, 1])
    return accuracy, auc

def train_and_evaluate():
    X, y = prepare_data_from_db()
    if X is None or len(X) < 100: 
        return False, 
    
    # Split data
    X_train, X_test, y_train, y_test = train_test_split(X, y, test_size=0.2, random_state=42)

    scaler = StandardScaler()
    X_train = scaler.fit_transform(X_train)
    X_test = scaler.transform(X_test)

    with open(scaler_path, 'wb') as f:
        pickle.dump(scaler, f)
    
    os.system(f'dvc add {scaler_path}')
    os.system('dvc push')
    
    # Train new model
    from sklearn.ensemble import RandomForestClassifier
    new_model = RandomForestClassifier(n_estimators=100, random_state=42)
    new_model.fit(X_train, y_train)
    
    # Evaluate new model
    new_accuracy, new_auc = evaluate_model(new_model, X_test, y_test)
    
    # Load and evaluate existing model
    with open(model_path, 'rb') as f:
        existing_model = pickle.load(f)
    existing_accuracy, existing_auc = evaluate_model(existing_model, X_test, y_test)
    
    # Compare performance
    min_improvement = 0.02
    if new_auc > existing_auc + min_improvement:
        # Save new model
        with open(model_path, 'wb') as f:
            pickle.dump(new_model, f)
        
        # Version the model with DVC
        os.system(f'dvc add {model_path}')
        os.system('dvc push')
        
        return True, f"Model updated. New AUC: {new_auc:.3f} (improved from {existing_auc:.3f})"
    
    return False, f"No significant improvement. New AUC: {new_auc:.3f} vs Existing AUC: {existing_auc:.3f}" 