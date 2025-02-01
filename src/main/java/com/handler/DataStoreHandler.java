package com.handler;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.LambdaLogger;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import software.amazon.awssdk.services.dynamodb.model.AttributeValue;
import software.amazon.awssdk.services.dynamodb.model.PutItemResponse;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

//https://github.com/awsdocs/aws-doc-sdk-examples/blob/main/javav2/usecases/creating_lambda_apigateway
//https://github.com/awsdocs/aws-doc-sdk-examples/blob/main/javav2/usecases/creating_lambda_apigateway/src/main/java/com/aws/example/ScanEmployees.java
//https://github.com/awsdocs/aws-doc-sdk-examples/blob/main/javav2/usecases/pam_source_files/src/main/java/com/example/photo/services/DynamoDBService.java
//https://github.com/awsdocs/aws-doc-sdk-examples/blob/main/javav2/example_code/dynamodb/src/main/java/com/example/dynamodb
//https://github.com/awsdocs/aws-doc-sdk-examples/blob/main/javav2/usecases/creating_lambda_ppe/src/main/java/com/example/ppe/PPEHandler.java

public class DataStoreHandler implements RequestHandler<UserRecord, String>{

    static {
        //Initialize Dynamo Db
        DataService.initDynamoDbClient();
    }
    @Override
    /*
     * Takes in an InputRecord, which contains two integers and a String.
     * Logs the String, then returns the sum of the two Integers.
     */
    public String handleRequest(UserRecord event, Context context)
    {
        try{
            LambdaLogger logger = context.getLogger();
            logger.log("Request Object is : " + event.toString());


            //Persist data
            //PutItemEnhancedResponse<CompareYourselfModel> dbResult = DataService.persistData(event, context);
            //logger.log(dbResult.toString());
            List<NotesModel> values = new ArrayList<>();
            PutItemResponse response = DataService.persistData(event, context);
            if(response!=null) {
                Map<String, AttributeValue> responseItem = response.attributes();
                logger.log("Consumed capacity is : " + response.consumedCapacity());
                logger.log("Response is : " + response.toString());
                logger.log("Response userid is : " + response.getValueForField("userid", NotesModel.class));
                for(Map.Entry<String, AttributeValue> entry : responseItem.entrySet()) {
                    AttributeValue val = entry.getValue();
                    logger.log(val.toString());
                }
                return DataService.TABLE_NAME + " was successfully updated. The request id is " + response.responseMetadata().requestId();
            }

            //Record response = new UserRecord(dbResult.attributes().getUserid(), dbResult.attributes().getAge(), dbResult.attributes().getHeight(), dbResult.attributes().getIncome());

            //Return saved values
            //return response.toString();
            return "added";
        } catch (Exception ase) {
            System.out.println("Error Message ::    " + ase.getMessage());
        }
        return null;
    }
}

record UserRecord(String user_id, Integer timestamp, String note_id) {
}
