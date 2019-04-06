package br.com.ufpb.dcx.si.crud;

import java.util.Collections;
import java.util.Map;

import org.apache.log4j.Logger;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;

import br.com.ufpb.dcx.si.ApiGatewayResponse;
import br.com.ufpb.dcx.si.Response;
import br.com.ufpb.dcx.si.dao.ProdutoDAO;
import br.com.ufpb.dcx.si.model.Produto;

public class GetProdutoHandler implements RequestHandler<Map<String,Object>,ApiGatewayResponse> {

	private ProdutoDAO produtoDAO;
	private final Logger logger = Logger.getLogger(this.getClass());

	@Override
	public ApiGatewayResponse handleRequest(Map<String, Object> input, Context context) {

		try {
			produtoDAO = new ProdutoDAO();
			// get input 'pathParameters'
			Map<String, String> pathParameters = (Map<String, String>) input.get("pathParameters");
			String produtoId = pathParameters.get("id");

			// get Produto to ID
			Produto produto = produtoDAO.get(produtoId);

			
			if (produto != null) {
				/** 
 				*  SUCCESS RESPONSE SERVICE
 				* @return success msg.(200)
				*/
				return ApiGatewayResponse.builder().setStatusCode(200).setObjectBody(produto)
				.setHeaders(
						Collections.singletonMap("X-Powered-By:", "AWS Lambda & Serverless"))
				.build();
				

			} else {
				/**
				 *  NOT FOUND RESPONSE SERVICE
				 * @return NOT FOUND Produto msg. (404)
				*/
				return ApiGatewayResponse.builder().setStatusCode(404)
				.setObjectBody("Produto com o id: '" + produtoId + "' não encontrado.").setHeaders(Collections
						.singletonMap("X-Powered-By:", "AWS Lambda & Serverless"))
				.build();
			}
		} catch (Exception ex) {
			/**
 * 
 *  ERROR RESPONSE SERVICE
 * @return error msg. (500)
*/

        logger.error("Erro interno no servidor! " + ex);

        Response responseBody = new Response("Erro interno no servidor! ", input);
        return ApiGatewayResponse.builder().setStatusCode(500).setObjectBody(responseBody)
                .setHeaders(
                        Collections.singletonMap("X-Powered-By:", "AWS Lambda & Serverless"))
                .build();
		}
	}

}