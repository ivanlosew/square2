package square;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import square.domain.Sync;
import square.repos.SyncRepo;

import java.util.Map;

@Controller
public class IndexController {

    @Autowired
    private SyncRepo syncRepo;

    @GetMapping("/")
    public String index(Model model) {
        return "index";
    }

    @GetMapping("/all")
    public @ResponseBody Iterable<Sync> getAllUsers (Map<String, Object> model) {
        Iterable<Sync> sync = syncRepo.findAll();
        model.put("sync", sync);
        return sync;
    }

    @GetMapping("/get")
    public @ResponseBody String position (@RequestParam int id) {
        return syncRepo.findById(id).get().getValue();
    }

    @PostMapping("/add")
    public @ResponseBody String add(@RequestParam(defaultValue = "0") int id, @RequestParam(defaultValue = "0") String value) {
         Sync sync = new Sync(id, value);
         syncRepo.save(sync);
         return "add";
    }

    @RequestMapping("/add")
    public String add(Map<String, Object> model) {
        return "add";
    }

}
