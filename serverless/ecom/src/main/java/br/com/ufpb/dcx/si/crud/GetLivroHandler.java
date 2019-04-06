package br.com.ufpb.dcx.si.crud;

import java.util.Collections;
import java.util.Map;

import org.apache.log4j.Logger;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;

import br.com.ufpb.dcx.si.ApiGatewayResponse;
import br.com.ufpb.dcx.si.Response;
import br.com.ufpb.dcx.si.dao.LivroDAO;
import br.com.ufpb.dcx.si.model.Livro;

public class GetLivroHandler implements RequestHandler<Map<String,Object>,ApiGatewayResponse> {

	private LivroDAO livroDAO;
	private final Logger logger = Logger.getLogger(this.getClass());

	@Override
	public ApiGatewayResponse handleRequest(Map<String, Object> input, Context context) {

		try {
			livroDAO = new LivroDAO();
			// get input 'pathParameters'
			Map<String, String> pathParameters = (Map<String, String>) input.get("pathParameters");
			String livroId = pathParameters.get("id");

			// get Livro to ID
			Livro livro = livroDAO.get(livroId);

			
			if (livro != null) {
				/** 
 				*  SUCCESS RESPONSE SERVICE
 				* @return success msg.(200)
				*/
				return ApiGatewayResponse.builder().setStatusCode(200).setObjectBody(livro)
				.setHeaders(
						Collections.singletonMap("X-Powered-By:", "AWS Lambda & Serverless"))
				.build();
				

			} else {
				/**
				 *  NOT FOUND RESPONSE SERVICE
				 * @return NOT FOUND Livro msg. (404)
				*/
				return ApiGatewayResponse.builder().setStatusCode(404)
				.setObjectBody("Livro com o id: '" + livroId + "' não encontrado.").setHeaders(Collections
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