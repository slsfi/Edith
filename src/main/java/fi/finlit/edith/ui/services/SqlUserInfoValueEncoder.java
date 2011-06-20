/**
 * 
 */
package fi.finlit.edith.ui.services;

import org.apache.tapestry5.ValueEncoder;

import fi.finlit.edith.dto.UserInfo;

public class SqlUserInfoValueEncoder implements ValueEncoder<UserInfo> {

    @Override
    public String toClient(UserInfo value) {
        return value.getUsername();
    }

    @Override
    public UserInfo toValue(String clientValue) {
        return new UserInfo(clientValue);
    }

}