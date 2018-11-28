package com.gg.rcmals.service.movie;

import com.gg.rcmals.domain.MovieRating;
import org.apache.spark.api.java.JavaRDD;
import org.apache.spark.ml.recommendation.ALSModel;
import org.apache.spark.sql.Dataset;
import org.apache.spark.sql.Row;
import org.apache.spark.sql.SparkSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.SmartLifecycle;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Component;
import scala.Serializable;

/**
 * @author daigouwei
 * @date 2018/11/22
 */
@Component
public class MovieSparkDriver implements SmartLifecycle, Serializable {
    private static final Logger LOG = LoggerFactory.getLogger("service");
    private static final long serialVersionUID = -7586918037465559578L;

    @Autowired
    private MovieParser movieParser;

    @Autowired
    private MovieRecommend movieRecommend;

    @Override
    public boolean isAutoStartup() {
        return true;
    }

    @Override
    public void stop(Runnable runnable) {

    }

    @Override
    public void start() {
        LOG.info("SparkRcmDriver start...");
        SparkSession sparkSession = SparkSession.builder().appName("RECOMMEND ALS").master("local[*]").getOrCreate();
        LOG.info("Movie parser...");
        Dataset<String> movieDataset = movieParser
            .readBehaviorFile(sparkSession, new ClassPathResource("src/main/resources/data/movie").getPath());
        JavaRDD<MovieRating> movieRatingRDD = movieDataset.javaRDD().map(line -> movieParser.parseBehaviorLog(line));
        LOG.info("Movie recommend::split data...");
        Dataset<Row>[] movieRatingRowArr = movieRecommend.splitData(sparkSession, movieRatingRDD);
        LOG.info("Movie recommend::generate ALS model...");
        ALSModel model = movieRecommend.generateModel(movieRatingRowArr[0]);
        LOG.info("Movie recommend::test ALS model...");
        Double rmse = movieRecommend.testModelByRmse(model, movieRatingRowArr[1]);
        LOG.info("rmse score==========={}", rmse);
        LOG.info("Movie recommend::recommend result...");
        movieRecommend.showRcmResults(model);
        LOG.info("recommend over!!!");
        sparkSession.close();
    }

    @Override
    public void stop() {

    }

    @Override
    public boolean isRunning() {
        return false;
    }

    @Override
    public int getPhase() {
        return 0;
    }
}
