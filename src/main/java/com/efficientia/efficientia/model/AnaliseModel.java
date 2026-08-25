package com.efficientia.efficientia.model;
//testando
import java.time.LocalDateTime;
import lombok.*;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class AnaliseModel {
    private int id;
    private AnalistaModel analistaModel;
    private TrajetoModel trajetoModel;
    private LocalDateTime dataAnalise;
    private String statusAnalise; //pode virar enum
    private String observacao; //pode virar um txt

    public void setStatusAnalise(String statusAnalise) {
        this.statusAnalise = statusAnalise;
    }

    public void setObservacao(String observacao) {
        this.observacao = observacao;
    }
}
