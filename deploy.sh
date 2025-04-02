set -e

REGION=us-east-1
BUCKET=cloud-formation-serverless-deployments
KEY=lambdas/S3TriggeredJavaLambda/java-file-op-1.0-SNAPSHOT.zip
STACK_NAME=rekog-java-file

# 1. Clean + Build fat JAR
mvn clean install
mvn clean package

# 2. Rename JAR to .zip (AWS requires .zip for Java Lambda deploys)
cp target/*-dependencies.jar java-file-op-1.0-SNAPSHOT.zip

# 3. Upload to S3
aws s3 cp java-file-op-1.0-SNAPSHOT.zip s3://$BUCKET/$KEY --region $REGION

# 4. Deploy CloudFormation stack
aws cloudformation deploy \
  --template-file config.yaml \
  --stack-name $STACK_NAME \
  --capabilities CAPABILITY_NAMED_IAM \
  --region $REGION
