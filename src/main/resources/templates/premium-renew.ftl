<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Subscription Renewal Confirmation</title>
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
            color: #28a745;
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
        <h2 class="header">Subscription Renewal Confirmation</h2>
        <p>Dear <strong>${name}</strong>,</p>
        <p>We are happy to inform you that your <strong>AutoRia Premium</strong> subscription has been successfully renewed.</p>

        <p><strong>Renewal Details:</strong></p>
        <ul>
            <li><strong>Subscription Plan:</strong> Premium</li>
            <li><strong>Renewal Date:</strong> ${renewal_date}</li>
            <li><strong>Next Billing Date:</strong> ${next_billing_date}</li>
        </ul>

        <p>Your access to exclusive features and premium support remains uninterrupted. You can continue enjoying the benefits of being a Premium member.</p>

        <p>If you have any questions or need assistance, feel free to <a href="mailto:support@autoria.com" class="button">Contact Support</a>.</p>

        <p class="note">If you did not authorize this renewal, please contact us immediately.</p>

        <p>Thank you for continuing to trust AutoRia!</p>

        <p>With respect,</p>
        <p><strong>AutoRia Team</strong></p>
    </div>
</body>
</html>
