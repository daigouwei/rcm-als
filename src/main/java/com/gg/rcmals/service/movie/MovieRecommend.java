package com.gg.rcmals.service.movie;

import com.gg.rcmals.domain.MovieRating;
import org.apache.spark.api.java.JavaRDD;
import org.apache.spark.ml.evaluation.RegressionEvaluator;
import org.apache.spark.ml.recommendation.ALS;
import org.apache.spark.ml.recommendation.ALSModel;
import org.apache.spark.sql.Dataset;
import org.apache.spark.sql.Row;
import org.apache.spark.sql.SparkSession;
import org.springframework.stereotype.Component;
import scala.Serializable;

/**
 * @author daigouwei
 * @date 2018/11/22
 */
@Component
public class MovieRecommend implements Serializable {
    private static final long serialVersionUID = -7750667307232698502L;

    public Dataset<Row>[] splitData(SparkSession sparkSession, JavaRDD<MovieRating> movieRatingRDD) {
        //生成训练数据和测试数据, [0]为训练数据，[1]为测试数据
        Dataset<Row> movieRatingRow = sparkSession.createDataFrame(movieRatingRDD, MovieRating.class);
        Dataset<Row>[] movieRatingRowArr = movieRatingRow.randomSplit(new double[] {0.8, 0.2});
        return movieRatingRowArr;
    }

    public ALSModel generateModel(Dataset<Row> movieTrain) {
        //生成model
        ALS als =
            new ALS().setMaxIter(5).setRegParam(0.01).setUserCol("userId").setItemCol("movieId").setRatingCol("rating");
        ALSModel model = als.fit(movieTrain);
        return model;
    }

    public Double testModelByRmse(ALSModel model, Dataset<Row> movieTest) {
        //Note we set cold start strategy to 'drop' to ensure we don't get NaN evaluation metrics
        model.setColdStartStrategy("drop");
        Dataset<Row> predictions = model.transform(movieTest);
        RegressionEvaluator evaluator =
            new RegressionEvaluator().setMetricName("rmse").setLabelCol("rating").setPredictionCol("prediction");
        Double rmse = evaluator.evaluate(predictions);
        return rmse;
    }

    public void showRcmResults(ALSModel model) {
        //为用户推荐10个电影
        Dataset<Row> userRcms = model.recommendForAllUsers(10);
        userRcms.show(false);
        //为电影推荐10个用户
        Dataset<Row> movieRcms = model.recommendForAllItems(10);
        movieRcms.show(false);
    }

}
