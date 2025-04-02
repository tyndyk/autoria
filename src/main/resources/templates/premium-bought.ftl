<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Premium Subscription Confirmation</title>
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
        <h2 class="header">Thank You for Upgrading to Premium!</h2>
        <p>Dear <strong>${name}</strong>,</p>
        <p>We’re excited to let you know that your payment for the <strong>Premium</strong> subscription has been successfully processed!</p>

        <p>With your Premium account, you now have access to exclusive features, including:</p>
        <ul>
            <li>Priority customer support</li>
            <li>Advanced filters and tools</li>
            <li>Exclusive deals and promotions</li>
            <li>And much more!</li>
        </ul>

        <p>Thank you for supporting AutoRia. We’re thrilled to have you as a Premium member!</p>

        <p>If you have any questions or need help, feel free to <a href="mailto:support@autoria.com" class="button">Contact Support</a>.</p>

        <p class="note">If you did not purchase the Premium subscription, please contact our support team immediately.</p>

        <p>With respect,</p>
        <p><strong>AutoRia Team</strong></p>
    </div>
</body>
</html>
