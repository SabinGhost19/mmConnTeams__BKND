package com.sabinghost19.teamslkghostapp.dto.registerRequest.mappers;

import com.sabinghost19.teamslkghostapp.dto.registerRequest.ReactionDTO;
import com.sabinghost19.teamslkghostapp.model.Channel;
import com.sabinghost19.teamslkghostapp.model.Message;
import com.sabinghost19.teamslkghostapp.model.Reaction;
import com.sabinghost19.teamslkghostapp.model.User;
import org.springframework.stereotype.Component;

@Component
public class ReactionMapper {


    public ReactionDTO toDTO(Reaction reaction) {
        if (reaction == null) {
            return null;
        }

        ReactionDTO dto = new ReactionDTO();
        dto.setId(reaction.getId());
        dto.setMessageId(reaction.getMessage().getId());
        dto.setUserId(reaction.getUser().getId());

        if (reaction.getChannel() != null) {
            dto.setChannelId(reaction.getChannel().getId());
        } else if (reaction.getMessage() != null && reaction.getMessage().getChannel() != null) {
            dto.setChannelId(reaction.getMessage().getChannel().getId());
        }

        dto.setReactionType(reaction.getReactionType());

        return dto;
    }


    public Reaction toEntity(ReactionDTO dto, Message message, User user, Channel channel) {
        if (dto == null) {
            return null;
        }

        return Reaction.builder()
                .message(message)
                .user(user)
                .channel(channel)
                .reactionType(dto.getReactionType())
                .build();
    }


    public void updateEntityFromDTO(ReactionDTO dto, Reaction reaction, Channel channel) {
        if (dto == null || reaction == null) {
            return;
        }

        reaction.setReactionType(dto.getReactionType());
        reaction.setChannel(channel);
    }
}