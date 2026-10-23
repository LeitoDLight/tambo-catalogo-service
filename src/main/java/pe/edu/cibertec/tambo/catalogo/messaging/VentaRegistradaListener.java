package pe.edu.cibertec.tambo.catalogo.messaging;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.AmqpRejectAndDontRequeueException;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;
import pe.edu.cibertec.tambo.catalogo.config.RabbitConfig;
import pe.edu.cibertec.tambo.catalogo.service.StockService;
import pe.edu.cibertec.tambo.catalogo.service.StockService.StockBajo;

@Component
@RequiredArgsConstructor
public class VentaRegistradaListener {

    private static final Logger log = LoggerFactory.getLogger(VentaRegistradaListener.class);

    private final StockService stockService;
    private final CatalogoPublisher publisher;

    @RabbitListener(queues = RabbitConfig.COLA_VENTA_REGISTRADA)
    public void recibir(String mensaje) {
        log.info("Recibido {}: {}", RabbitConfig.RK_VENTA_REGISTRADA, mensaje);

        Long ventaId;
        Map<Long, Integer> cantidades;
        try {
            String[] partes = mensaje.trim().split("\\|", 2);
            if (partes.length != 2 || partes[1].isBlank()) {
                throw new IllegalArgumentException("se esperaba ventaId|productoId:cantidad,...");
            }
            ventaId = Long.parseLong(partes[0].trim());
            cantidades = leerItems(partes[1]);
        } catch (IllegalArgumentException e) { // incluye NumberFormatException
            log.error("Mensaje con formato inválido '{}': {}", mensaje, e.getMessage());
            throw new AmqpRejectAndDontRequeueException("Formato inválido", e);
        }

        List<StockBajo> alertas;
        try {
            // La transacción termina aquí: los mensajes salen solo si el stock se guardó.
            alertas = stockService.descontar(ventaId, cantidades);
        } catch (IllegalStateException e) {
            log.error("No se pudo procesar la venta {}: {}", ventaId, e.getMessage());
            throw new AmqpRejectAndDontRequeueException(e.getMessage(), e);
        }

        alertas.forEach(publisher::publicarStockBajo);
        publisher.publicarVentaProcesada(ventaId);
    }

    private Map<Long, Integer> leerItems(String texto) {
        Map<Long, Integer> cantidades = new LinkedHashMap<>();
        for (String item : texto.split(",")) {
            String[] par = item.trim().split(":");
            if (par.length != 2) {
                throw new IllegalArgumentException("item inválido '" + item + "'");
            }
            Long productoId = Long.parseLong(par[0].trim());
            int cantidad = Integer.parseInt(par[1].trim());
            if (cantidad <= 0) {
                throw new IllegalArgumentException("cantidad debe ser mayor a 0 en '" + item + "'");
            }
            cantidades.merge(productoId, cantidad, Integer::sum);
        }
        return cantidades;
    }
}
