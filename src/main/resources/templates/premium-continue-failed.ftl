<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Subscription Renewal Payment Failed</title>
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
        <h2 class="header">Subscription Renewal Payment Failed</h2>
        <p>Dear <strong>${name}</strong>,</p>
        <p>We were unable to process the payment for the upcoming renewal of your <strong>AutoRia Premium</strong> subscription for the next month.</p>

        <p><strong>Renewal Details:</strong></p>
        <ul>
            <li><strong>Subscription Plan:</strong> Premium</li>
            <li><strong>Payment Attempt Date:</strong> ${payment_attempt_date}</li>
        </ul>

        <p>In order to continue enjoying your Premium benefits, please update your payment information and complete the payment process:</p>

        <a href="http://localhost:3000/payment-method" class="button">Update Payment Details</a>

        <p class="note">If you need assistance, feel free to <a href="mailto:support@autoria.com" class="button">Contact Support</a>.</p>

        <p>We hope to resolve this issue as soon as possible so you can continue benefiting from your Premium subscription.</p>

        <p>With respect,</p>
        <p><strong>AutoRia Team</strong></p>
    </div>
</body>
</html>
