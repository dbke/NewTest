import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Spring {
    public List<Node> springLayout(List<Node> nodes,List<Link> links) {
        //2计算每次迭代局部区域内两两节点间的斥力所产生的单位位移（一般为正值）
        int area = 1024 * 768;
        // k为每个点所占面积开根号
        double k = Math.sqrt(area / (double)nodes.size());
        double  diffx, diffy, diff;
        Map<Integer,Double> dispx = new HashMap<Integer,Double>();

        Map<Integer,Double> dispy = new HashMap<Integer,Double>();
            
        int ejectfactor = 6;

        for (int v = 0; v < nodes.size(); v++) {
            dispx.put(nodes.get(v).getId(), 0.0);
            dispy.put(nodes.get(v).getId(), 0.0);
            for (int u = 0; u < nodes.size();  u++) {
                if (u != v) {
                    diffx = nodes.get(v).getX() - nodes.get(u).getX();
                    diffy = nodes.get(v).getY() - nodes.get(u).getY();

                    diff = Math.sqrt(diffx * diffx + diffy * diffy);//两点之间的距离
                 
                    if (diff < 30)
                        ejectfactor = 5;//不同距离影响因素

                    if (diff > 0 && diff < 250) {
                        int id = nodes.get(v).getId();
                        dispx.put(id,dispx.get(id) + diffx / diff * k * k / diff * ejectfactor);
                        dispy.put(id,dispy.get(id) + diffy / diff * k * k / diff* ejectfactor);
                    }
                }
            }
        }
        //3. 计算每次迭代每条边的引力对两端节点所产生的单位位移（一般为负值）
        int condensefactor = 3;
        Node visnodeS = null, visnodeE = null;
        
        for (int e = 0; e < links.size(); e++) {
            int eStartID = links.get(e).getSource();
            int eEndID = links.get(e).getTarget();
            visnodeS = getNodeById(nodes,eStartID);
            visnodeE = getNodeById(nodes,eEndID);

            diffx = visnodeS.getX() - visnodeE.getX(); 
            diffy = visnodeS.getY() - visnodeE.getY();
            diff = Math.sqrt(diffx * diffx + diffy * diffy);//

            dispx.put(eStartID,dispx.get(eStartID) - diffx * diff / k * condensefactor);
            dispy.put(eStartID,dispy.get(eStartID) - diffy * diff / k* condensefactor);
            dispx.put(eEndID,dispx.get(eEndID) + diffx * diff / k * condensefactor);
            dispy.put(eEndID,dispy.get(eEndID) + diffy * diff / k * condensefactor);
        }
     
        //set x,y
        int maxt = 4 ,maxty = 3;
        for (int v = 0; v < nodes.size(); v++) {
            Node node = nodes.get(v);
            Double dx = dispx.get(node.getId());
            Double dy = dispy.get(node.getId());
            int disppx = (int) Math.floor(dx);
            int disppy = (int) Math.floor(dy);
            if (disppx < -maxt)
                disppx = -maxt;
            if (disppx > maxt)
                disppx = maxt;
            if (disppy < -maxty)
                disppy = -maxty;
            if (disppy > maxty)
                disppy = maxty;
            
            node.setX((node.getX()+disppx));
            node.setY((node.getY()+disppy));
        }    
        return nodes;
    }
    
    private Node getNodeById(List<Node> nodes,int id){
        for(Node node:nodes){
            if(node.getId()==(id)){
                return node;
            }
        }
        return null;
    }
}
