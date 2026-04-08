package pcd.StartingPool;

import org.junit.Assert;
import org.junit.Test;
import pcd.startingPoool.model.game.V2d;



public class V2dTest {

    @Test
    public void sum_shouldAddComponents() {
        V2d a = new V2d(1.5, -2.0);
        V2d b = new V2d(0.5, 3.0);

        V2d result = a.sum(b);

        Assert.assertEquals(2.0, result.x(), 1e-9);
        Assert.assertEquals(1.0, result.y(), 1e-9);
    }

    @Test
    public void getNormalized_shouldReturnUnitVector() {
        V2d v = new V2d(3.0, 4.0);

        V2d normalized = v.getNormalized();

        Assert.assertEquals(1.0, normalized.abs(), 1e-9);
    }
}

