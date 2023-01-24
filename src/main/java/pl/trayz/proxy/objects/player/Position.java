package pl.trayz.proxy.objects.player;

import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * @Author: Trayz
 **/

/**
 * Player position.
 */
@AllArgsConstructor
@Data
public class Position {

    private final double x,y,z;

}
