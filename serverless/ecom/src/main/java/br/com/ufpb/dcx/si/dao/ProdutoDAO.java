package br.com.ufpb.dcx.si.dao;

import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapper;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapperConfig;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBQueryExpression;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBScanExpression;
import com.amazonaws.services.dynamodbv2.datamodeling.PaginatedQueryList;
import com.amazonaws.services.dynamodbv2.model.AttributeValue;

import br.com.ufpb.dcx.si.model.Produto;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;

import org.apache.log4j.Logger;

public class ProdutoDAO{

    // does get in the variable with the table name in the 'serverless.yml' file
    private static final String ECOM_PRODUTO_TABLE_NAME=System.getenv("ECOM_PRODUTO_TABLE_NAME");

    private static DynamoDBAdapter db_adapter;
    private final AmazonDynamoDB client;
    private final DynamoDBMapper mapper;

    private Logger logger=Logger.getLogger(this.getClass());

    public ProdutoDAO(){

        // build in mapper configurations
        DynamoDBMapperConfig mapperConfig = DynamoDBMapperConfig.builder()
                .withTableNameOverride(new DynamoDBMapperConfig.TableNameOverride(ECOM_PRODUTO_TABLE_NAME)).build();

        // Does 'get' no db_adapter

        this.db_adapter = DynamoDBAdapter.getInstance();
        this.client = this.db_adapter.getDbClient();

        // Create DynamoDB mapper configurations
        this.mapper = this.db_adapter.createDbMapper(mapperConfig);
    }

        // CRUD methods
    public Boolean ifTableExists(){
        return this.client.describeTable(ECOM_PRODUTO_TABLE_NAME).getTable().getTableStatus().equals("ACTIVE");
    }

    public List <Produto> list() throws IOException {
        DynamoDBScanExpression scanExp = new DynamoDBScanExpression();
        List<Produto> resultados = this.mapper.scan(Produto.class,scanExp);
        for(Produto l:resultados){
            logger.info("Produto - list(): "+ l.toString());
        }
        return resultados;
    }

    public Produto get(String id)throws IOException{
        
        Produto produto = null;
        
        HashMap<String,AttributeValue> av = new HashMap<String,AttributeValue>();
        av.put(":v1",new AttributeValue().withS(id));

        DynamoDBQueryExpression<Produto> queryExp = new DynamoDBQueryExpression <Produto>()
                    .withKeyConditionExpression("id = :v1").withExpressionAttributeValues(av);

        PaginatedQueryList<Produto> resultado = this.mapper.query(Produto.class,queryExp);
        if(resultado.size()>0){
            produto = resultado.get(0);
            logger.info("Produto - get(): produto - " + produto.toString());
        } else {
            logger.info("Produto - get(): produto - Não encontrado.");
        }
        return produto;

        }

    public void save (Produto produto) throws IOException {
        logger.info("Produto - save(): " + produto.toString());
        this.mapper.save(produto);
    }

    public Boolean delete(String id) throws IOException{
        Produto produto = null;
        
        // Check if produto exists, before deleting
        produto = get(id);
        if(produto != null){
            logger.info("Produto - delete(): " + produto.toString());
            this.mapper.delete(produto);
        } else {
            logger.info("Produto - delete(): produto - não existe.");
            return false;
        }
            return true;
    }
}