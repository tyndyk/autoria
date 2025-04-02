<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Invitation to Join</title>
    <style>
        body {
            font-family: Arial, sans-serif;
            background-color: #f4f4f4;
            color: #333;
            text-align: center;
            padding: 20px;
        }
        .container {
            background: #ffffff;
            padding: 20px;
            border-radius: 8px;
            box-shadow: 0 0 10px rgba(0, 0, 0, 0.1);
            display: inline-block;
            max-width: 500px;
        }
        h2 {
            color: #007bff;
        }
        .btn {
            display: inline-block;
            background-color: #007bff;
            color: #ffffff;
            padding: 10px 20px;
            text-decoration: none;
            border-radius: 5px;
            font-weight: bold;
            margin-top: 15px;
        }
    </style>
</head>
<body>
    <div class="container">
        <h2>You're Invited to Join Our Platform</h2>
        <p>Hello,</p>
        <p>You have been invited to create an account as a <strong>${role}</strong> on our platform.</p>
        <p>Click the button below to complete your registration:</p>
        <a href="http://localhost:3000/auth/register/${role}?code=${code}" class="btn">Register Now</a>
        <p>If the button does not work, you can use the following link:</p>
        <p><a href="http://localhost:3000/auth/register/${role}?code=${code}">http://localhost:3000/auth/register/${role}?code=${code}</a></p>
        <p>Thank you!</p>
    </div>
</body>
</html>
