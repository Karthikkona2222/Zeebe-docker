package com.aaseya.AIS;
 
public class OperateConstant {
	
    // Base configuration (change this if the region or cluster ID changes)
    public static final String CAMUNDA_OPERATE_CLUSTER_BASE = "https://sin-1.operate.camunda.io/0fc1dec6-2e1a-411a-94bf-9cb49b24430f";
 
    // OAuth configuration
    public static final String CLIENT_ID = "PEDR-9qLiXRG9a-knPW2_nmw6-MHPF9f";
    public static final String CLIENT_SECRET = "ViTEcztjtjQmiC-YNguVJBVV7JIwGJHih5fVda6kj.fbG6-d8OrWo94BrO5wiD_6";
    public static final String TOKEN_URL = "https://login.cloud.camunda.io/oauth/token";
    public static final String GRANT_TYPE = "client_credentials";
    public static final String AUDIENCE = "operate.camunda.io";
 
    // Keycloak config
    public static final String KEYCLOAK_CLIENT_ID = "aaseyainspectionsolution";
    public static final String KEYCLOAK_CLIENT_SECRET = "nTJJR3VDiLS0GQGySp2aBcNUUHRQIUVn";
 
    // Operate API URLs
    public static final String SEARCH_PROCESS_INSTANCES = CAMUNDA_OPERATE_CLUSTER_BASE + "/v1/process-instances/search";
    public static final String GET_PROCESS_INSTANCE_BY_KEY = CAMUNDA_OPERATE_CLUSTER_BASE + "/v1/process-instances";
    public static final String GET_VARIABLES_BY_INSTANCE_ID = CAMUNDA_OPERATE_CLUSTER_BASE + "/v1/variables/search";
    public static final String SEARCH_VARIABLES = 
            CAMUNDA_OPERATE_CLUSTER_BASE + "/v1/variables/search";
}
 
	
	
//	public static String CLIENT_ID = "operate";
//    public static String TOKEN_URL = "http://10.13.1.180:18080/auth/realms/camunda-platform/protocol/openid-connect/token";
//    public static String CLIENT_SECRET = "XALaRPl5qwTEItdwCMiPS62nVpKs7dL7";
//    public static String GRANT_TYPE = "client_credentials";
//    public static String SEARCH_PROCESS_INSTANCES = "http://10.13.1.180:8081/v1/process-instances/search";
//    public static String Get_PROCESS_INSTANCE_BY_KEY = "http://10.13.1.180:8081/v1/process-instances";
//    public static String KEYCLOAK_CLIENT_ID = "aaseyainspectionsolution";
//    public static String KEYCLOAK_CLIENT_SECRET = "nTJJR3VDiLS0GQGySp2aBcNUUHRQIUVn";
	
//}