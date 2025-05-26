package backend.eventsphere.review.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ReviewCreateDto {
    @NotNull
    private UUID eventId;
    
    @NotNull
    private UUID userId;
    
    @NotBlank
    private String comment;
    
    @Min(1)
    @Max(5)
    private int rating;
}