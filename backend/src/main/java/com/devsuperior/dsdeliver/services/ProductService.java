package com.devsuperior.dsdeliver.services;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.devsuperior.dsdeliver.dto.ProductDTO;
import com.devsuperior.dsdeliver.entities.Product;
import com.devsuperior.dsdeliver.repositories.ProductRespository;

@Service
public class ProductService {

	@Autowired
	private ProductRespository repository;
	
	//Uma forma de fazer uma injecao de dependencia desacoplada
	/*public ProductService(ProductRespository repository) {
		this.repository = repository;
	}*/
	//Porem estamos num Framework que contem uma injecao de dependencia desacoplada que faz tudo
	
	//Aqui eh para garantir a transacao, porem apenas em modo leitura, para evitar o lock no BD
	@Transactional(readOnly = true)
	public List<ProductDTO> findAll(){
		List<Product> list = repository.findAllByOrderByNameAsc();
		return list.stream().map(x -> new ProductDTO(x)).collect(Collectors.toList());
	}
}
