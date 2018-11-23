package com.gg.rcmals.service.movie;

import com.gg.rcmals.domain.MovieRating;
import org.apache.commons.lang.StringUtils;
import org.apache.spark.sql.Dataset;
import org.apache.spark.sql.SparkSession;
import org.springframework.stereotype.Component;
import scala.Serializable;

/**
 * @author daigouwei
 * @date 2018/11/22
 */
@Component
public class MovieParser implements Serializable {
    private static final long serialVersionUID = 370800676322849175L;

    public Dataset<String> readBehaviorFile(SparkSession sparkSession, String path) {
        return sparkSession.read().textFile(path);
    }

    public MovieRating parseBehaviorLog(String behaviorLog) {
        if (StringUtils.isBlank(behaviorLog)) {
            return null;
        }
        String[] movieRatingArr = behaviorLog.split("\t");
        if (4 != movieRatingArr.length) {
            return null;
        }
        MovieRating movieRating = new MovieRating();
        movieRating.setUserId(Integer.valueOf(movieRatingArr[0]));
        movieRating.setMovieId(Integer.valueOf(movieRatingArr[1]));
        movieRating.setRating(Float.valueOf(movieRatingArr[2]));
        movieRating.setTimestamp(Long.valueOf(movieRatingArr[3]));
        return movieRating;
    }
}
