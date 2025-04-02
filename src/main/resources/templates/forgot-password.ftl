<!DOCTYPE html>
<html lang="">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <style>
        h2 {
            color: #007bff;
        }
        body {
            font-family: Arial, sans-serif;
            background-color: #f4f4f4;
            color: #333;
            padding: 20px;
        }
        .container {
            background-color: #ffffff;
            padding: 20px;
            border-radius: 8px;
            box-shadow: 0 0 10px rgba(0, 0, 0, 0.1);
            display: inline-block;
            max-width: 500px;
        }
        .button {
            background-color: #007bff;
            color: #fff;
            padding: 10px 20px;
            text-decoration: none;
            border-radius: 5px;
            font-weight: bold;
            display: inline-block;
            margin-top: 15px;
        }
        .note {
            color: darkred;
        }
    </style>
</head>
<body>
    <div class="container">
        <h2>Welcome to AutoRia!</h2>
        <p>Greetings from the AutoRia Team!</p>
        <p>Your account with the email <strong>${email}</strong> requested a password reset at <strong>${time}</strong>.</p>

        <p>If this was you, please click the link below to reset your password:</p>
        <a href="http://localhost:3000/auth/reset-password?code=${code}" class="button">Change Password</a>

        <p class="note">If you did not request this password reset, please ignore this email.</p>

        <p>With respect,</p>
        <p><strong>AutoRia Team</strong></p>
    </div>
</body>
</html>
