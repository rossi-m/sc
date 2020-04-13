
import com.leyou.pojo.UserInfo;
import com.leyou.utils.JwtUtils;
import com.leyou.utils.RsaUtils;
import org.junit.Before;
import org.junit.Test;

import java.security.PrivateKey;
import java.security.PublicKey;

public class JwtTest {
    private static String PUBLIC_KEY_PATH="D:\\rsa.pub";
    private static String PRIVATE_KEY_PATH="D:\\rsa.pri";
    private PublicKey publicKey;
    private PrivateKey privateKey;

    /**
     * 生成公钥和私钥
     */
    @Test
    public void testRsa() throws Exception {
        //根据密文"234"，生存rsa公钥和私钥,并写入指定文件
        RsaUtils.generateKey(PUBLIC_KEY_PATH, PRIVATE_KEY_PATH, "234");
    }

    /**
     * 读取公钥和私钥
     */
    @Before
    public void testGetRsa() throws Exception {
        //从文件中读取公钥
        this.publicKey = RsaUtils.getPublicKey(PUBLIC_KEY_PATH);
        //从文件中读取私钥
        this.privateKey = RsaUtils.getPrivateKey(PRIVATE_KEY_PATH);
    }

    /**
     * 生成token
     */
    @Test
    public void testGenerateToken() throws Exception {
        // 生成token
        String token = JwtUtils.generateToken(new UserInfo(1L, "Lee34"), privateKey, 5);
        System.out.println("token = " + token);
    }

    /**
     * 解析token
     */
    @Test
    public void testParseToken() throws Exception {
        //上边生成的token
        String token = "eyJhbGciOiJSUzI1NiJ9.eyJpZCI6MSwidXNlcm5hbWUiOiJMZWUzNCIsImV4cCI6MTU4NjQwMTMyOH0.iXVNbFKSbrLdAAyPxQ7oBYdyw3Q7eUG7cAQ9khrJdifIq_p2hKbHdx8kjUOee24b3aY8n1_D8Fz5z9dhct7Y_mOF7XGaRdFrjLTJ_NVGtrol1nxPsazO0n9Np0px73ePTPFvNkBC0Hvo8hSsT2QV6aCtOmWI-_b6A1Q-C0klmdY";

        // 解析token
        UserInfo user = JwtUtils.getInfoFromToken(token, publicKey);
        System.out.println("id: " + user.getId());
        System.out.println("userName: " + user.getUsername());
    }
}
