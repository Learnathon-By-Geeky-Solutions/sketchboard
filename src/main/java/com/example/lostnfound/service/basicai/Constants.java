package com.example.lostnfound.service.basicai;
import com.example.lostnfound.model.Post;
import com.example.lostnfound.util.DatabaseUtils;

public class Constants {

        private Constants() {
        }

        public static final String TABLEINFO = DatabaseUtils.getTableAndColumnNames(Post.class);
        public static final String HEADERMSGFORGEMINI = """
                Hey Gemini, Read the user's message given below and database table info then create a postgresql query by following some steps given at the end of this message
                """;
        public static final String FOOTERMSGFORGEMINI = """
                Steps:
                1. First convert the msg to english (if user msg is not in english) identify something. what item is lost by user? Where user lost her item? When the user lost her item?
                
                2. If their is any information gap (less information) then just ignore those field which information is not given. Give an postgresql query according to the exact information.
                
                3. If user says in her msg the she lost an item then the status should be 'Found' always. Similarly, If user say's in her message that she found an item then status should be 'Lost' always. you must follow this step.
                
                4. Please only give me the sql query only. No extra text please.
                
                5. handle date and time very carefully. Don't create any wrong postgresql query and don't make any syntax error.
                
                6. make query as simple as possible.
                
                7. Don't make hard query. First check item's in category then in title if not found in category then check in description. But check place where item lost and time strictly (if given in user's msg). [Use Like and % % for better match for item's and place.
                Use OR for checking item in category,title and description. and use AND for place and time and item's found in any field (if enough info given by user)]
                """;
}