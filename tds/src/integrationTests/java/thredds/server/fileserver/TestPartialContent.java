package thredds.server.fileserver;

import static com.google.common.truth.Truth.assertThat;

import java.lang.invoke.MethodHandles;
import java.util.ArrayList;
import java.util.List;
import javax.servlet.http.HttpServletResponse;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import thredds.test.util.TestOnLocalServer;
import thredds.util.ContentType;

@RunWith(Parameterized.class)
public class TestPartialContent {
  private static final Logger logger = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

  @Parameterized.Parameters(name = "{0}")
  static public List<long[]> getTestParameters() {
    final List<long[]> result = new ArrayList<>();

    result.add(new long[] {0, 0});
    result.add(new long[] {0, 1});
    result.add(new long[] {42, 1000});
    result.add(new long[] {0, 1000000000});

    return result;
  }

  @Parameterized.Parameter()
  public long[] byteRange;

  @Test
  public void shouldReturnPartialContent() {
    final String path = "fileServer/scanLocal/mydata1.nc";
    final long maxSize = 66832;
    final String contentType = ContentType.netcdf.toString();
    final String endpoint = TestOnLocalServer.withHttpPath(path);

    final byte[] content = TestOnLocalServer.getContent(null, endpoint,
        new int[] {HttpServletResponse.SC_PARTIAL_CONTENT}, contentType, byteRange);
    assertThat(content).isNotNull();

    final long expectedLength = Math.min(byteRange[1] - byteRange[0] + 1, maxSize);
    assertThat(content.length).isEqualTo(expectedLength);
  }
}
