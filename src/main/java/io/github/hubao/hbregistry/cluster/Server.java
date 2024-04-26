package io.github.hubao.hbregistry.cluster;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

/*
 * Desc: Registry server instance.
 *
 * @author hubao
 * @see 2024/4/25 21:03
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(of = {"url"})
public class Server {
    private String url;
    private boolean status;
    private boolean leader;
    private long version;
}
