package pl.goralewski.artur.golfium.model;

/**
 * Created by Artur Góralewski on 12/05/2015.
 */
public class HolePlay {

    private Integer id;
    private Integer numberOfHits;
    private Integer gameId;
    private Integer holeId;

    public HolePlay(Integer id, Integer numberOfHits, Integer gameId, Integer holeId) {
        this.id = id;
        this.numberOfHits = numberOfHits;
        this.gameId = gameId;
        this.holeId = holeId;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getNumberOfHits() {
        return numberOfHits;
    }

    public void setNumberOfHits(Integer numberOfHits) {
        this.numberOfHits = numberOfHits;
    }

    public Integer getGameId() {
        return gameId;
    }

    public void setGameId(Integer gameId) {
        this.gameId = gameId;
    }

    public Integer getHoleId() {
        return holeId;
    }

    public void setHoleId(Integer holeId) {
        this.holeId = holeId;
    }
}
