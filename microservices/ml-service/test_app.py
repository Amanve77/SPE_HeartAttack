import json
import pytest
from app import app

@pytest.fixture
def client():
    app.config['TESTING'] = True
    with app.test_client() as client:
        yield client

def test_predict_endpoint_success(client):
    test_data = {
        "age": 65,
        "sex": 1,
        "cp": 0,
        "trestbps": 140,
        "chol": 289,
        "fbs": 0,
        "restecg": 0,
        "thalach": 140,
        "exang": 0,
        "oldpeak": 2.1,
        "slope": 1,
        "ca": 0,
        "thal": 3
    }
    
    response = client.post('/predict',
                         data=json.dumps(test_data),
                         content_type='application/json')
    
    assert response.status_code == 200
    data = json.loads(response.data)
    assert 'prediction' in data
    assert isinstance(data['prediction'], int)
    assert data['prediction'] in [0, 1]

def test_predict_endpoint_missing_data(client):
    test_data = {
        "age": 65,
        "sex": 1
        # Missing other required fields
    }
    
    response = client.post('/predict',
                         data=json.dumps(test_data),
                         content_type='application/json')
    
    assert response.status_code == 400

def test_predict_endpoint_invalid_values(client):
    test_data = {
        "age": -1,  # Invalid age
        "sex": 1,
        "cp": 0,
        "trestbps": 140,
        "chol": 289,
        "fbs": 0,
        "restecg": 0,
        "thalach": 140,
        "exang": 0,
        "oldpeak": 2.1,
        "slope": 1,
        "ca": 0,
        "thal": 3
    }
    
    response = client.post('/predict',
                         data=json.dumps(test_data),
                         content_type='application/json')
    
    assert response.status_code == 400

def test_health_check(client):
    response = client.get('/health')
    assert response.status_code == 200
    data = json.loads(response.data)
    assert data['status'] == 'healthy' 