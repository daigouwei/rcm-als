package com.gg.rcmals.service.guide;

import org.apache.spark.ml.linalg.Vector;
import org.apache.spark.ml.linalg.VectorUDT;
import org.apache.spark.ml.linalg.Vectors;
import org.apache.spark.ml.stat.ChiSquareTest;
import org.apache.spark.ml.stat.Correlation;
import org.apache.spark.ml.stat.Summarizer;
import org.apache.spark.sql.Column;
import org.apache.spark.sql.Dataset;
import org.apache.spark.sql.Row;
import org.apache.spark.sql.RowFactory;
import org.apache.spark.sql.SparkSession;
import org.apache.spark.sql.types.DataTypes;
import org.apache.spark.sql.types.Metadata;
import org.apache.spark.sql.types.StructField;
import org.apache.spark.sql.types.StructType;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.Arrays;
import java.util.List;

/**
 * @author guowei
 * @date 2019/2/21
 */
@Component
public class BasicStatistics {
    private SparkSession spark;

    public static void main(String[] args) {
        BasicStatistics basicStatistics = new BasicStatistics();
        basicStatistics.init();
        basicStatistics.correlation();
        basicStatistics.hypothesisTesting();
    }

    @PostConstruct
    public void init() {
        spark = SparkSession.builder().appName("guide").master("local[*]").getOrCreate();
    }

    public void correlation() {
        List<Row> data = Arrays.asList(RowFactory.create(Vectors.sparse(4, new int[]{0, 3}, new double[]{1.0, -2.0})),
                RowFactory.create(Vectors.dense(4.0, 5.0, 0.0, 3.0)),
                RowFactory.create(Vectors.dense(6.0, 7.0, 0.0, 8.0)),
                RowFactory.create(Vectors.sparse(4, new int[]{0, 3}, new double[]{9.0, 1.0})));

        StructType schema = new StructType(
                new StructField[]{new StructField("features", new VectorUDT(), false, Metadata.empty()),});

        Dataset<Row> df = spark.createDataFrame(data, schema);
        df.show();
        Row r1 = Correlation.corr(df, "features").head();
        System.out.println("Pearson correlation matrix:\n" + r1.get(0).toString());

        Row r2 = Correlation.corr(df, "features", "spearman").head();
        System.out.println("Spearman correlation matrix:\n" + r2.get(0).toString());
    }

    public void hypothesisTesting(){
        List<Row> data = Arrays.asList(
                RowFactory.create(0.0, Vectors.dense(0.5, 10.0)),
                RowFactory.create(0.0, Vectors.dense(1.5, 20.0)),
                RowFactory.create(1.0, Vectors.dense(1.5, 30.0)),
                RowFactory.create(0.0, Vectors.dense(3.5, 30.0)),
                RowFactory.create(0.0, Vectors.dense(3.5, 40.0)),
                RowFactory.create(1.0, Vectors.dense(3.5, 40.0))
        );

        StructType schema = new StructType(new StructField[]{
                new StructField("label", DataTypes.DoubleType, false, Metadata.empty()),
                new StructField("features", new VectorUDT(), false, Metadata.empty()),
        });

        Dataset<Row> df = spark.createDataFrame(data, schema);
        df.show();
        Row r = ChiSquareTest.test(df, "features", "label").head();
        System.out.println("pValues: " + r.get(0).toString());
        System.out.println("degreesOfFreedom: " + r.getList(1).toString());
        System.out.println("statistics: " + r.get(2).toString());
    }

    public void summarizer(){
        List<Row> data = Arrays.asList(
                RowFactory.create(Vectors.dense(2.0, 3.0, 5.0), 1.0),
                RowFactory.create(Vectors.dense(4.0, 6.0, 7.0), 2.0)
        );

        StructType schema = new StructType(new StructField[]{
                new StructField("features", new VectorUDT(), false, Metadata.empty()),
                new StructField("weight", DataTypes.DoubleType, false, Metadata.empty())
        });

        Dataset<Row> df = spark.createDataFrame(data, schema);

        Row result1 = df.select(Summarizer.metrics("mean", "variance")
                .summary(new Column("features"), new Column("weight")).as("summary"))
                .select("summary.mean", "summary.variance").first();
        System.out.println("with weight: mean = " + result1.<Vector>getAs(0).toString() +
                ", variance = " + result1.<Vector>getAs(1).toString());

        Row result2 = df.select(
                Summarizer.mean(new Column("features")),
                Summarizer.variance(new Column("features"))
        ).first();
        System.out.println("without weight: mean = " + result2.<Vector>getAs(0).toString() +
                ", variance = " + result2.<Vector>getAs(1).toString());
    }
}
