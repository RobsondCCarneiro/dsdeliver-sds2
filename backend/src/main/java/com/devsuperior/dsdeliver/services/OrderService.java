package com.devsuperior.dsdeliver.services;

import java.time.Instant;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.devsuperior.dsdeliver.dto.OrderDTO;
import com.devsuperior.dsdeliver.dto.ProductDTO;
import com.devsuperior.dsdeliver.entities.Order;
import com.devsuperior.dsdeliver.entities.OrderStatus;
import com.devsuperior.dsdeliver.entities.Product;
import com.devsuperior.dsdeliver.repositories.OrderRespository;
import com.devsuperior.dsdeliver.repositories.ProductRespository;

@Service
public class OrderService {

	@Autowired
	private OrderRespository repository;
	
	//Aqui serve para acessar os produtos que cada pedido estah associado.
	@Autowired
	private ProductRespository productRepository;
	
	//Aqui eh para ler o Banco de Dados (Comando GET no PostMan)
	@Transactional(readOnly = true)
	public List<OrderDTO> findAll(){
		List<Order> list = repository.findOrdersWithProducts();
		return list.stream().map(x -> new OrderDTO(x)).collect(Collectors.toList());
	}
	
	
	//Aqui eh para inserir no Banco de Dados (comando POST no PostMan)
	@Transactional
	public OrderDTO insert(OrderDTO dto){
		Order order = new Order(null, dto.getAddress(), dto.getLatitude(), dto.getLongitude(),
				Instant.now(), OrderStatus.PENDING);
		
		/*
		 * Para associar os pedidos que jah estavam armazenados no Banco de Dados com os novos,
		 * eh preciso fazer esse laco aqui. O metodo POST eh para inserir arquivos no BD.
		 * NAO PODE ASSOCIAR DTO COM ENTIDADE, TEM QUE INSTANCIAR UMA ENTIDADE PARA CADA
		 * ProductDTO.
		 */
		for(ProductDTO p : dto.getProducts()) {
			//O getOne instancia um produto sem acessar o BD. SALVA AS ASSOCIACOES QUE CADA PRODUTO TEM AO SEU PEDIDO
			Product product = productRepository.getOne(p.getId());
			order.getProducts().add(product);
		}
		
		order = repository.save(order);
		return new OrderDTO(order);
	}
	
	//Aqui eh para alterar o banco de dados
	@Transactional
	public OrderDTO setDelivered(Long id){
		Order order = repository.getOne(id);
		order.setStatus(OrderStatus.DELIVERED);
		order = repository.save(order);
		return new OrderDTO(order);
	}
}
