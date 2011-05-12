/**
 * 
 */
package fi.finlit.edith.ui.services;

import org.apache.tapestry5.ValueEncoder;

import fi.finlit.edith.domain.UserInfo;

public class UserInfoValueEncoder implements ValueEncoder<UserInfo> {
    
    @Override
    public String toClient(UserInfo value) {
        return value.getUsername();
    }

    @Override
    public UserInfo toValue(String clientValue) {
        return new UserInfo(clientValue);
    }
    
}