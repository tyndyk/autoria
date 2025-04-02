<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Subscription Cancellation Confirmation</title>
    <style>
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
        .header {
            color: #dc3545;
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
            color: #6c757d;
        }
    </style>
</head>
<body>
    <div class="container">
        <h2 class="header">Subscription Cancelled</h2>
        <p>Dear <strong>${name}</strong>,</p>
        <p>We regret to inform you that your <strong>AutoRia Premium</strong> subscription has been successfully canceled.</p>

        <p><strong>Cancellation Details:</strong></p>
        <ul>
            <li><strong>Subscription Plan:</strong> Premium</li>
            <li><strong>Cancellation Date:</strong> ${cancellation_date}</li>
        </ul>

        <p>If you wish to re-activate your Premium subscription, please <a href="http://localhost:3000/renew" class="button">Click Here to Renew</a> or visit your account settings.</p>

        <p>We are sorry to see you go, but we hope to welcome you back in the future.</p>

        <p class="note">If you have any questions or concerns about your cancellation, feel free to <a href="mailto:support@autoria.com" class="button">Contact Support</a>.</p>

        <p>With respect,</p>
        <p><strong>AutoRia Team</strong></p>
    </div>
</body>
</html>
