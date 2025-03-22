#!/bin/bash
awslocal sqs create-queue --queue-name order-created-queue
awslocal sns create-topic --name order-created-topic
awslocal sqs get-queue-attributes --queue-url http://sqs.us-east-1.localhost.localstack.cloud:4566/000000000000/order-created-queue --attribute-name QueueArn
awslocal sns subscribe --topic-arn arn:aws:sns:us-east-1:000000000000:order-created-topic --protocol sqs --notification-endpoint arn:aws:sqs:us-east-1:000000000000:order-created-queue
awslocal sqs create-queue --queue-name payment-completed-queue
awslocal sns create-topic --name payment-completed-topic
awslocal sqs get-queue-attributes --queue-url http://sqs.us-east-1.localhost.localstack.cloud:4566/000000000000/payment-completed-queue --attribute-name QueueArn
awslocal sns subscribe --topic-arn arn:aws:sns:us-east-1:000000000000:payment-completed-topic --protocol sqs --notification-endpoint arn:aws:sqs:us-east-1:000000000000:payment-completed-queue
awslocal sqs create-queue --queue-name payment-failed-queue
awslocal sns create-topic --name payment-failed-topic
awslocal sqs get-queue-attributes --queue-url http://sqs.us-east-1.localhost.localstack.cloud:4566/000000000000/payment-failed-queue --attribute-name QueueArn
awslocal sns subscribe --topic-arn arn:aws:sns:us-east-1:000000000000:payment-failed-topic --protocol sqs --notification-endpoint arn:aws:sqs:us-east-1:000000000000:payment-failed-queue
awslocal sns list-subscriptions
