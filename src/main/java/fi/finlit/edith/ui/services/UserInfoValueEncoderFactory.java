/**
 * 
 */
package fi.finlit.edith.ui.services;

import org.apache.tapestry5.ValueEncoder;
import org.apache.tapestry5.services.ValueEncoderFactory;

import fi.finlit.edith.dto.UserInfo;

public class UserInfoValueEncoderFactory implements ValueEncoderFactory<UserInfo> {

    @Override
    public ValueEncoder<UserInfo> create(Class<UserInfo> type) {
        return new UserInfoValueEncoder();
    }

}