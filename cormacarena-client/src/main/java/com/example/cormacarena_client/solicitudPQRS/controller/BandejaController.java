package com.example.cormacarena_client.solicitudPQRS.controller;


import com.example.cormacarena_client.solicitudPQRS.dto.MensajeDTO;
import com.example.cormacarena_client.solicitudPQRS.model.Bandeja;
import com.example.cormacarena_client.solicitudPQRS.repository.BandejaRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/bandeja")
public class BandejaController {

    private final BandejaRepository bandejaRepository;

    public BandejaController(BandejaRepository bandejaRepository) {
        this.bandejaRepository = bandejaRepository;
    }

    @PostMapping
    public ResponseEntity<?> recibirMensaje(@RequestBody MensajeDTO mensajeDTO) {
        Bandeja bandeja = new Bandeja(mensajeDTO.getMensaje());
        bandejaRepository.save(bandeja);
        return ResponseEntity.ok().build();
    }

    @GetMapping
    public ResponseEntity<?> obtenerMensajes() {
        return ResponseEntity.ok(bandejaRepository.findAll());
    }


}
