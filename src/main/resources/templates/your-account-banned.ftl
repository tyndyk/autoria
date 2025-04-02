<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Account Banned Notification</title>
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
            color: #d9534f;
        }
        .warning {
            color: darkred;
            font-weight: bold;
        }
    </style>
</head>
<body>
    <div class="container">
        <h1 class="header">Account Banned</h1>
        <p>Hello, <strong>${name}</strong>,</p>
        <p>Our platform upholds a respectful community environment. After a review by our team, we have detected offensive content associated with your account.</p>
        <p class="warning">We regret to inform you that your account <strong>${email}</strong> has been permanently banned.</p>
        <p>If you believe this action was taken in error, you may contact our support team for further clarification.</p>
        <p>Best regards,</p>
        <p><strong>AutoRia Team</strong></p>
    </div>
</body>
</html>
