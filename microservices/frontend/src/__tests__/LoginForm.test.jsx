import React from 'react';
import { render, screen, fireEvent, waitFor } from '@testing-library/react';
import { BrowserRouter } from 'react-router-dom';
import LoginForm from '../components/LoginForm';
import API from '../services/api';

// Mock the API module
jest.mock('../services/api', () => ({
    post: jest.fn()
}));

// Mock react-router-dom hooks
jest.mock('react-router-dom', () => ({
    ...jest.requireActual('react-router-dom'),
    useParams: () => ({ role: 'patient' }),
    useNavigate: () => jest.fn()
}));

describe('LoginForm', () => {
    beforeEach(() => {
        // Clear mocks before each test
        jest.clearAllMocks();
    });

    test('renders login form', () => {
        render(
            <BrowserRouter>
                <LoginForm />
            </BrowserRouter>
        );
        expect(screen.getByPlaceholderText(/email/i)).toBeInTheDocument();
        expect(screen.getByPlaceholderText(/password/i)).toBeInTheDocument();
        expect(screen.getByRole('button', { name: /login/i })).toBeInTheDocument();
    });

    test('handles successful login', async () => {

        render(
            <BrowserRouter>
                <LoginForm />
            </BrowserRouter>
        );

        const mockResponse = {
            data: {
                token: 'fake-token',
                tokenType: 'Bearer',
                doctorId: '1',
                patientId: '1'
            }
        };
        API.post.mockResolvedValueOnce(mockResponse);


        fireEvent.change(screen.getByPlaceholderText(/email/i), {
            target: { value: 'test@example.com' }
        });
        fireEvent.change(screen.getByPlaceholderText(/password/i), {
            target: { value: 'password123' }
        });

        fireEvent.click(screen.getByRole('button', { name: /login/i }));

        await waitFor(() => {
            expect(API.post).toHaveBeenCalledWith('/auth/login', {
                email: 'test@example.com',
                password: 'password123',
                role: 'PATIENT'
            });
        });
    });

    test('handles login error', async () => {
        render(
            <BrowserRouter>
                <LoginForm />
            </BrowserRouter>
        );
        const mockError = {
            response: {
                data: {
                    message: 'Invalid credentials'
                }
            }
        };
        API.post.mockRejectedValueOnce(mockError);

        fireEvent.change(screen.getByPlaceholderText(/email/i), {
            target: { value: 'wrong@example.com' }
        });
        fireEvent.change(screen.getByPlaceholderText(/password/i), {
            target: { value: 'wrongpassword' }
        });

        fireEvent.click(screen.getByRole('button', { name: /login/i }));

        await waitFor(() => {
            expect(API.post).toHaveBeenCalledWith('/auth/login', {
                email: 'wrong@example.com',
                password: 'wrongpassword',
                role: 'PATIENT'
            });
        });
        await waitFor(() => {
            expect(screen.getByText(/Invalid credentials/i)).toBeInTheDocument();
        });
    });
}); 