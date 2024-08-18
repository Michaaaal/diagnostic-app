package michal.malek.diagnosticsapp.core.mappers;

import michal.malek.diagnosticsapp.medic_data.models.PersonalDataDTO;
import michal.malek.diagnosticsapp.medic_data.models.UserData;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

@Mapper
public abstract class UserDataMapper {

    public static final UserDataMapper INSTANCE = Mappers.getMapper(UserDataMapper.class);

    public abstract PersonalDataDTO userDataToPersonalDataDTO(UserData userData);
}
