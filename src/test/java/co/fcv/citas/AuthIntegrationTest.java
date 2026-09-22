package co.fcv.citas;
import co.fcv.citas.auth.AuthDtos; import org.junit.jupiter.api.Test; import org.springframework.beans.factory.annotation.Autowired; import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc; import org.springframework.boot.test.context.SpringBootTest; import org.springframework.http.MediaType; import org.springframework.test.web.servlet.MockMvc; import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*; import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
@SpringBootTest @AutoConfigureMockMvc class AuthIntegrationTest {
 @Autowired MockMvc mvc;
 @Test void registerLoginAndRefreshRevocationWork() throws Exception {
  String register="{\"firstName\":\"Ana\",\"lastName\":\"Prueba\",\"documentType\":\"CC\",\"documentNumber\":\"1001\",\"email\":\"ana@example.test\",\"phone\":\"3000000000\",\"password\":\"password123\"}";
  String response=mvc.perform(post("/api/auth/register").contentType(MediaType.APPLICATION_JSON).content(register)).andExpect(status().isCreated()).andExpect(jsonPath("$.accessToken").isString()).andExpect(jsonPath("$.refreshToken").isString()).andReturn().getResponse().getContentAsString();
  String refresh=com.jayway.jsonpath.JsonPath.read(response,"$.refreshToken");
  String login="{\"email\":\"ana@example.test\",\"password\":\"password123\"}";
  String access=mvc.perform(post("/api/auth/login").contentType(MediaType.APPLICATION_JSON).content(login)).andExpect(status().isOk()).andExpect(jsonPath("$.user.roles[0]").value("USER")).andReturn().getResponse().getContentAsString();
  String accessToken=com.jayway.jsonpath.JsonPath.read(access,"$.accessToken");
  mvc.perform(get("/api/me").header("Authorization","Bearer "+accessToken)).andExpect(status().isOk()).andExpect(jsonPath("$.email").value("ana@example.test"));
  mvc.perform(get("/api/me")).andExpect(status().isUnauthorized());
  mvc.perform(post("/api/auth/refresh").contentType(MediaType.APPLICATION_JSON).content("{\"refreshToken\":\""+refresh+"\"}" )).andExpect(status().isOk());
  mvc.perform(post("/api/auth/logout").contentType(MediaType.APPLICATION_JSON).content("{\"refreshToken\":\""+refresh+"\"}" )).andExpect(status().isNoContent());
  mvc.perform(post("/api/auth/refresh").contentType(MediaType.APPLICATION_JSON).content("{\"refreshToken\":\""+refresh+"\"}" )).andExpect(status().isUnauthorized());
 }
 @Test void invalidCredentialsDoNotIssueTokens() throws Exception {mvc.perform(post("/api/auth/login").contentType(MediaType.APPLICATION_JSON).content("{\"email\":\"nobody@example.test\",\"password\":\"wrong\"}" )).andExpect(status().isUnauthorized()).andExpect(jsonPath("$.accessToken").doesNotExist());}
}
