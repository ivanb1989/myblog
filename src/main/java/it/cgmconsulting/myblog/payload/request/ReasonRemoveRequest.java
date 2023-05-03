package it.cgmconsulting.myblog.payload.request;

import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;

import java.time.LocalDate;

@Getter
public class ReasonRemoveRequest{

    @NotBlank  @Size(max=30)
    private String reasonId;

    @NotNull @FutureOrPresent
    private LocalDate endDate;
}
