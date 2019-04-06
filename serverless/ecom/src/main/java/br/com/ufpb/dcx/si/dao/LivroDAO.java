package br.com.ufpb.dcx.si.dao;

import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapper;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapperConfig;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBQueryExpression;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBScanExpression;
import com.amazonaws.services.dynamodbv2.datamodeling.PaginatedQueryList;
import com.amazonaws.services.dynamodbv2.model.AttributeValue;

import br.com.ufpb.dcx.si.model.Livro;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;

import org.apache.log4j.Logger;

public class LivroDAO{

    // does get in the variable with the table name in the 'serverless.yml' file
    private static final String ECOM_LIVRO_TABLE_NAME=System.getenv("ECOM_LIVRO_TABLE_NAME");

    private static DynamoDBAdapter db_adapter;
    private final AmazonDynamoDB client;
    private final DynamoDBMapper mapper;

    private Logger logger=Logger.getLogger(this.getClass());

    public LivroDAO(){

        // build in mapper configurations
        DynamoDBMapperConfig mapperConfig = DynamoDBMapperConfig.builder()
                .withTableNameOverride(new DynamoDBMapperConfig.TableNameOverride(ECOM_LIVRO_TABLE_NAME)).build();

        // Does 'get' no db_adapter

        this.db_adapter = DynamoDBAdapter.getInstance();
        this.client = this.db_adapter.getDbClient();

        // Create DynamoDB mapper configurations
        this.mapper = this.db_adapter.createDbMapper(mapperConfig);
    }

        // CRUD methods
    public Boolean ifTableExists(){
        return this.client.describeTable(ECOM_LIVRO_TABLE_NAME).getTable().getTableStatus().equals("ACTIVE");
    }

    public List <Livro> list() throws IOException {
        DynamoDBScanExpression scanExp = new DynamoDBScanExpression();
        List<Livro> resultados = this.mapper.scan(Livro.class,scanExp);
        for(Livro l:resultados){
            logger.info("Livro - list(): "+ l.toString());
        }
        return resultados;
    }

    public Livro get(String id)throws IOException{
        
        Livro livro = null;
        
        HashMap<String,AttributeValue> av = new HashMap<String,AttributeValue>();
        av.put(":v1",new AttributeValue().withS(id));

        DynamoDBQueryExpression<Livro> queryExp = new DynamoDBQueryExpression <Livro>()
                    .withKeyConditionExpression("id = :v1").withExpressionAttributeValues(av);

        PaginatedQueryList<Livro> resultado = this.mapper.query(Livro.class,queryExp);
        if(resultado.size()>0){
            livro = resultado.get(0);
            logger.info("Livro - get(): livro - " + livro.toString());
        } else {
            logger.info("Livro - get(): livro - Não encontrado.");
        }
        return livro;

        }

    public void save (Livro livro) throws IOException {
        logger.info("Livro - save(): " + livro.toString());
        this.mapper.save(livro);
    }

    public Boolean delete(String id) throws IOException{
        Livro livro = null;
        
        // Check if livro exists, before deleting
        livro = get(id);
        if(livro != null){
            logger.info("Livro - delete(): " + livro.toString());
            this.mapper.delete(livro);
        } else {
            logger.info("Livro - delete(): livro - não existe.");
            return false;
        }
            return true;
    }
}