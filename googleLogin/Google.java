import com.nineder.net.web.SimpleHttps;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.NameValuePair;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.commons.httpclient.params.HttpClientParams;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * 參考文件 https://developers.google.com/identity/sign-in/web/backend-auth
 *
 * 利用id_token取得googleId
 */
public class Google {
  //取得google使用者基本資料位置
  private String googleUrl = "https://www.googleapis.com/oauth2/v3/tokeninfo";
  //利用code取得google token位置　
  private String googleTokenUri="https://www.googleapis.com/oauth2/v4/token";
  private String id_token = "";
  private String googleId = "";
  private JSONObject googleJson = null;

  private String clientId="=================clientId====================";
  private String clientSecret="===============clientSecret=====================";
  private String grantType="authorization_code";

  public Google() {

  }


  /**
   * 利用google回傳的 code 再向google要token
   * @param code
   * @param redirectUri
   * @return
   * @throws JSONException
   * 如果連線成功將會回傳
   * access_token 	A token that can be sent to a Google API.
   * id_token 	A JWT that contains identity information about the user that is digitally signed by Google.
   * expires_in 	The remaining lifetime of the access token.
   * token_type 	Identifies the type of token returned. At this time, this field always has the value Bearer.
   * refresh_token (optional) 	This field is only present if access_type=offline is included in the authentication request. For details, see Refresh tokens.
   */
  public JSONObject getGoogleToken(String code , String redirectUri){
    List<NameValuePair> NameValuePairList = new ArrayList<NameValuePair>();
    NameValuePairList.add(new NameValuePair("code", code));
    NameValuePairList.add(new NameValuePair("client_id", clientId));
    NameValuePairList.add(new NameValuePair("client_secret", clientSecret));
    NameValuePairList.add(new NameValuePair("redirect_uri", redirectUri));
    NameValuePairList.add(new NameValuePair("grant_type", grantType));
    String resString="";
    JSONObject googleToken=null;
    //需要自定header 就自行連線了
    try {
//      resString = SimpleHttps.getPageContent(url, NameValuePairList);
      HttpClient client = new HttpClient();
      HttpClientParams params = new HttpClientParams();

      List<NameValuePair> nameValuePairArrayList = NameValuePairList;
      client.getParams().setContentCharset("utf-8");
      params.setSoTimeout(30000);
      client.setParams(params);
      PostMethod post = new PostMethod(googleTokenUri);
      //取得token header 需要自訂
      post.setRequestHeader("Content-Type","application/x-www-form-urlencoded");
      if(nameValuePairArrayList.size()>0)
      post.setRequestBody((NameValuePair[])nameValuePairArrayList.toArray(new NameValuePair[0]));
      client.executeMethod(post);
      resString = post.getResponseBodyAsString();
      googleToken=new JSONObject(resString);
    }catch (JSONException je){
      je.printStackTrace();
    }catch (Exception e){
      e.printStackTrace();
    }
    return googleToken;
  }


  /**
   * 利用google回傳的 token 再向google要使用者資料
   * @param id_token
   * @return
   *
   *
  Claim           Provided    Description
  iss             always 	    The Issuer Identifier for the Issuer of the response. Always https://accounts.google.com or accounts.google.com for Google ID tokens.
  at_hash                     Access token hash. Provides validation that the access token is tied to the identity token. If the ID token is issued with an access token in the server flow, this is always included. This can be used as an alternate mechanism to protect against cross-site request forgery attacks, but if you follow Step 1 and Step 3 it is not necessary to verify the access token.
  email_verified 		          True if the user's e-mail address has been verified; otherwise false.
  sub 	          always 	    An identifier for the user, unique among all Google accounts and never reused. A Google account can have multiple emails at different points in time, but the sub value is never changed. Use sub within your application as the unique-identifier key for the user.
  azp 		                    The client_id of the authorized presenter. This claim is only needed when the party requesting the ID token is not the same as the audience of the ID token. This may be the case at Google for hybrid apps where a web application and Android app have a different client_id but share the same project.
  email 		                  The user's email address. This may not be unique and is not suitable for use as a primary key. Provided only if your scope included the string "email".

  profile 		                The URL of the user's profile page. Might be provided when:
                              The request scope included the string "profile"
                              The ID token is returned from a token refresh
                              When profile claims are present, you can use them to update your app's user records. Note that this claim is never guaranteed to be present.

  picture 		                The URL of the user's profile picture. Might be provided when:
                              The request scope included the string "profile"
                              The ID token is returned from a token refresh
                              When picture claims are present, you can use them to update your app's user records. Note that this claim is never guaranteed to be present.

  name 		                    The user's full name, in a displayable form. Might be provided when:
                              The request scope included the string "profile"
                              The ID token is returned from a token refresh
                              When name claims are present, you can use them to update your app's user records. Note that this claim is never guaranteed to be present.

  aud 	          always 	    Identifies the audience that this ID token is intended for. It must be one of the OAuth 2.0 client IDs of your application.
  iat 	          always 	    The time the ID token was issued, represented in Unix time (integer seconds).
  exp 	          always 	    The time the ID token expires, represented in Unix time (integer seconds).
  hd 		                      The hosted Google Apps domain of the user. Provided only if the user belongs to a hosted domain.
   */
  public JSONObject getGoogleIdToken(String id_token){
    List<NameValuePair> NameValuePairList = new ArrayList<NameValuePair>();
    NameValuePairList.add(new NameValuePair("id_token", id_token));
    String resString="";
    JSONObject googleIdToken=null;
    resString = connGoogle(googleUrl,NameValuePairList);
    try {
      googleIdToken = new JSONObject(resString);
    }catch (JSONException e){
      e.printStackTrace();
    }
    return googleIdToken;
  }
}
