import React from 'react';
import { useNavigate } from 'react-router-dom';
import Navbar from '../components/Navbar';
import '../App.css';


export default function Home() {
  const navigate = useNavigate();

  return (
    <>
    <Navbar/>
    <div className="bg-light text-dark">
      {/* Hero Section */}
      <div className="hero-background d-flex flex-column justify-content-center align-items-center vh-100 bg-gradient-primary text-center p-4 ">
        <h1 className="mb-3 text-primary display-4">Welcome to YourDost</h1>
        <p className="lead mb-4">Analyze heart health, get medical insights, and prevent risks.</p>
        <div className="d-flex gap-3">
          <button
            onClick={() => navigate('/login/patient')}
            className="btn btn-outline-primary btn-lg"
          >
            Login as Patient
          </button>
          <button
            onClick={() => navigate('/login/doctor')}
            className="btn btn-outline-success btn-lg"
          >
            Login as Doctor
          </button>
        </div>
      </div>

   
      <div className="container py-5">
        {/* Overview */}
        <section id="overview" className="mb-5">
          <h2 className="mb-3 text-primary">Overview</h2>
          <p>
          A heart attack occurs when the flow of blood to the heart is severely reduced or blocked. The blockage is usually due to a buildup of fat, cholesterol and other substances in the heart (coronary) arteries. The fatty, cholesterol-containing deposits are called plaques. The process of plaque buildup is called atherosclerosis.
          </p>
        </section>

        {/* Symptoms */}
        <section id="Symptoms" className="mb-5">
          <h2 className="mb-3 text-primary">Symptoms</h2>
          <ul>
            <li>Chest pain</li>
            <li>Discomfort in arm/back/jaw</li>
            <li>Cold sweat</li>
            <li>Fatigue</li>
            <li>Heartburn</li>
            <li>Dizziness</li>
            <li>Nausea</li>
            <li>Shortness of breath</li>
          </ul>
          <p>Women may have atypical symptoms...</p>
        </section>

        {/* Consulation */}
        <section id="Consulation" className="mb-5">
          <h2 className="mb-3 text-primary">Consultation</h2>
          <p>Call 911 if you suspect a heart attack. While waiting:</p>
          <ul>
            <li>Take nitroglycerin if prescribed</li>
            <li>Take aspirin only if advised</li>
          </ul>
          <h5 className="mt-4">CPR Instructions:</h5>
          <ul>
            <li>If untrained: Hands-only CPR (100–120 compressions/min)</li>
            <li>If trained: 30 compressions + 2 breaths</li>
          </ul>
        </section>

        {/* Causes */}
        <section id="Causes" className="mb-5">
          <h2 className="mb-3 text-primary">Causes</h2>
          <p>Most heart attacks are due to coronary artery disease...</p>
          <ul>
            <li>STEMI – complete blockage</li>
            <li>NSTEMI – partial or sometimes total blockage</li>
            <li>Coronary artery spasm</li>
            <li>COVID-19 and infections</li>
            <li>SCAD – tear in the artery</li>
          </ul>
        </section>

        {/* Risk Factors */}
        <section id="Risk-factors" className="mb-5">
          <h2 className="mb-3 text-primary">Risk Factors</h2>
          <ul>
            <li>Age & gender</li>
            <li>Smoking or tobacco</li>
            <li>High BP, cholesterol, diabetes</li>
            <li>Obesity, sedentary lifestyle</li>
            <li>Unhealthy diet, stress</li>
            <li>Family history & genetics</li>
            <li>Drug use, autoimmune conditions</li>
          </ul>
        </section>

        {/* Complications */}
        <section id="Complications" className="mb-5">
          <h2 className="mb-3 text-primary">Complications</h2>
          <ul>
            <li>Arrhythmias</li>
            <li>Cardiac arrest</li>
            <li>Heart failure</li>
            <li>Inflammation or pericarditis</li>
            <li>Cardiogenic shock</li>
          </ul>
        </section>

        {/* Prevention */}
        <section id="Prevention" className="mb-5">
          <h2 className="mb-3 text-primary">Prevention</h2>
          <ul>
            <li>Healthy diet & exercise</li>
            <li>Quit smoking</li>
            <li>Control BP, diabetes</li>
            <li>Regular medical checkups</li>
            <li>Know CPR and how to use AED</li>
          </ul>
        </section>
      </div>

      {/* Smooth Scroll Script */}
      <script dangerouslySetInnerHTML={{
        __html: `
          document.querySelectorAll('a[href^="#"]').forEach(anchor => {
            anchor.addEventListener('click', function (e) {
              e.preventDefault();
              const target = document.querySelector(this.getAttribute('href'));
              if (target) {
                target.scrollIntoView({ behavior: 'smooth' });
              }
            });
          });
        `
      }} />
    </div>
    <button
  onClick={() => window.scrollTo({ top: 0, behavior: 'smooth' })}
  className="btn btn-primary position-fixed bottom-0 end-0 m-4 shadow"
  style={{ zIndex: 1000 }}
>
  ↑ Top
</button>

    </>
  );
}
