package michal.malek.diagnosticsapp.diagnostics_part.mappers;

import michal.malek.diagnosticsapp.diagnostics_part.models.DiagnoseMessageEntity;
import michal.malek.diagnosticsapp.diagnostics_part.models.request.Message;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

import java.util.List;

@Mapper
public interface DiagnoseMessageMapper  {

    DiagnoseMessageMapper INSTANCE = Mappers.getMapper(DiagnoseMessageMapper.class);

    @Mapping(source = "message", target = "content")
    Message toMessage(DiagnoseMessageEntity diagnoseMessageEntity);


    List<Message> toMessageList(List<DiagnoseMessageEntity> diagnoseMessageEntities);


}
