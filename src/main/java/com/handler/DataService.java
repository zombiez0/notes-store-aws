package com.handler;

import com.amazonaws.services.lambda.runtime.Context;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbEnhancedClient;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbTable;
import software.amazon.awssdk.enhanced.dynamodb.TableSchema;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.dynamodb.DynamoDbClient;
import software.amazon.awssdk.services.dynamodb.model.*;

import java.util.HashMap;
import java.util.Map;

public class DataService {

    private static DynamoDbClient DYNAMO_DB;
    private static DynamoDbEnhancedClient ENHANCED_DYNAMODB_CLIENT;
    private static DynamoDbTable<NotesModel> DYNAMODB_TABLE;
    public static final Region REGION = Region.AP_SOUTH_1;

    public static final String TABLE_NAME = "NOTES_TABLE";

    private static final Logger LOGGER = LoggerFactory.getLogger(DataService.class);

    public static void initDynamoDbClient() {
        LOGGER.info("Initializing dynamo db");
        try{
            DYNAMO_DB = DynamoDbClient.builder()
                    .region(REGION)
                    .build();
            ENHANCED_DYNAMODB_CLIENT = DynamoDbEnhancedClient.builder().dynamoDbClient(DYNAMO_DB)
                    .build();
        }catch (Exception e) {
            LOGGER.error("Exception occurred in initializing dynamo db client", e);
        }
        DYNAMODB_TABLE = ENHANCED_DYNAMODB_CLIENT.table(TABLE_NAME, TableSchema.fromBean(NotesModel.class));
        LOGGER.info("Dynamo db initialized");
    }

//    public static PutItemEnhancedResponse<CompareYourselfModel> persistData(UserRecord userRecord, Context context){
//        context.getLogger().log("Adding requested items to table");
//        PutItemEnhancedResponse<CompareYourselfModel> response = DYNAMODB_TABLE.putItemWithResponse(PutItemEnhancedRequest.builder(CompareYourselfModel.class)
//                .item(new CompareYourselfModel(userRecord.userid(), userRecord.age(), userRecord.height(), userRecord.income()))
//                .returnConsumedCapacity(ReturnConsumedCapacity.TOTAL)
//                .build());
//        //DYNAMODB_TABLE.putItem(new CompareYourselfModel(userRecord.userid(), userRecord.age(), userRecord.height(), userRecord.income()));
//        //context.getLogger().log("PutItem call consumed [" + response.consumedCapacity().capacityUnits() + "] Write Capacity Unites (WCU)");
//        context.getLogger().log("Successfully added items to table");
//        return response;
//    }

//    public static void persistData(UserRecord userRecord, Context context){
//        context.getLogger().log("Adding requested items to table");
//        DYNAMODB_TABLE.putItem(new CompareYourselfModel(userRecord.userid(), userRecord.age(), userRecord.height(), userRecord.income()));
//        context.getLogger().log("Successfully added items to table");
//    }

    public static PutItemResponse persistData(UserRecord userRecord, Context context){
        context.getLogger().log("Adding requested items to table");

        PutItemResponse response = null;
        try {
            response = DYNAMO_DB.putItem(buildRequest(userRecord, context));
            context.getLogger().log((TABLE_NAME + " was successfully updated. The request id is "
                    + response.responseMetadata().requestId()));

        } catch (ResourceNotFoundException e) {
            context.getLogger().log("Error: The Amazon DynamoDB table \"%s\" can't be found.\n" +  TABLE_NAME);
            throw e;
        } catch (DynamoDbException e) {
            System.err.println(e.getMessage());
            throw e;
        }
        return response;
    }

    private static PutItemRequest buildRequest(UserRecord userRecord, Context context){
        context.getLogger().log("Building user request map");
        Map<String, AttributeValue> itemValues = new HashMap<>();
        itemValues.put("user_id" ,  AttributeValue.builder().s(userRecord.user_id()).build());
        itemValues.put("timestamp" ,  AttributeValue.builder().n(String.valueOf(userRecord.timestamp())).build());
        itemValues.put("note_id" ,  AttributeValue.builder().s(String.valueOf(userRecord.note_id())).build());

        return PutItemRequest.builder()
                .tableName(TABLE_NAME)
                .item(itemValues)
                .build();
    }

}
