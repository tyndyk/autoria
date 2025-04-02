<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Account Deletion Confirmation</title>
    <link rel="stylesheet" href="main.css">
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
        .header {
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
        <h1 class="header">Account Deleted</h1>
        <p>Hello, <strong>${name}</strong>,</p>
        <p>We’re sorry to see you leave our platform.</p>
        <p>Your account associated with <strong>${email}</strong> has been successfully deleted.</p>
        <p>If you'd like to return, you're always welcome back.</p>
        <p>Click below to create a new account anytime:</p>
        <a href="${register_url}" class="btn">Rejoin AutoRia</a>
        <p>Best regards,</p>
        <p><strong>AutoRia Team</strong></p>
    </div>
</body>
</html>
