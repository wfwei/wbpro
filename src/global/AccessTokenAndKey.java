package global;

public class AccessTokenAndKey {

	private static String[][] accessTokenAndKey = null;

	public static void init() {
		// 初始化accessTokenAndKey
		accessTokenAndKey = new String[10][2];
		accessTokenAndKey[0][0] = "9e332f1bc1f4b2af99eaf707e15e8085";
		accessTokenAndKey[0][1] = "0d888e63ecae123b378dc674c496986a";
		accessTokenAndKey[1][0] = "ca401a174dad293742fcdfa00a9095bf";
		accessTokenAndKey[1][1] = "78b36ce0d8685b0cfd6daecf849f085f";
		accessTokenAndKey[2][0] = "6bfef2c6f73ce025d257ad0a3381fdfa";
		accessTokenAndKey[2][1] = "6029cbc95b49a75eb61926027821f24a";
		accessTokenAndKey[3][0] = "1177fabe1ce07e1d7dad169f119371fe";
		accessTokenAndKey[3][1] = "b096cf839f0317ad1efb03b000faa49b";
		accessTokenAndKey[4][0] = "c777276d6e8f67668ec6f6ca6579f79b";
		accessTokenAndKey[4][1] = "773c5a5b91f75b6bbdd55b5366231009";
		accessTokenAndKey[5][0] = "d72761b6d746a25b0bded82b988e363e";
		accessTokenAndKey[5][1] = "e4fe36aad1d4c6702d236a819b0e20d1";
		accessTokenAndKey[6][0] = "127a920f7d4e886443042ee694062949";
		accessTokenAndKey[6][1] = "fbc99ff096d635f6e9de501c7646a81a";
	}
	
	public static String[][] getAccessTokenAndKey(){
		if(null == accessTokenAndKey){
			init();
		}
		return accessTokenAndKey;
	}
	
}
