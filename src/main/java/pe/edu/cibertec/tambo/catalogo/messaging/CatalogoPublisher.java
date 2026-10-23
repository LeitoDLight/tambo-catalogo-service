package pe.edu.cibertec.tambo.catalogo.messaging;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;
import pe.edu.cibertec.tambo.catalogo.config.RabbitConfig;
import pe.edu.cibertec.tambo.catalogo.service.StockService.StockBajo;

@Component
@RequiredArgsConstructor
public class CatalogoPublisher {

    private static final Logger log = LoggerFactory.getLogger(CatalogoPublisher.class);

    private final RabbitTemplate rabbitTemplate;

    public void publicarStockBajo(StockBajo s) {
        String mensaje = s.productoId() + "|" + s.stockActual() + "|" + s.nombre();
        rabbitTemplate.convertAndSend(RabbitConfig.EXCHANGE, RabbitConfig.RK_STOCK_BAJO, mensaje);
        log.info("Publicado {}: {}", RabbitConfig.RK_STOCK_BAJO, mensaje);
    }

    public void publicarVentaProcesada(Long ventaId) {
        String mensaje = String.valueOf(ventaId);
        rabbitTemplate.convertAndSend(RabbitConfig.EXCHANGE, RabbitConfig.RK_VENTA_PROCESADA, mensaje);
        log.info("Publicado {}: {}", RabbitConfig.RK_VENTA_PROCESADA, mensaje);
    }
}
