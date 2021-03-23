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
    public @ResponseBody
    Iterable<Sync> getAll(Map<String, Object> model) {
        Iterable<Sync> sync = syncRepo.findAll();
        model.put("sync", sync);
        return sync;
    }

    @GetMapping("/get")
    public @ResponseBody
    String value(@RequestParam int id) {
        return syncRepo.findById(id).get().getValue();
    }

    @PostMapping("/add")
    public @ResponseBody
    String add(@RequestParam(defaultValue = "0") int id, @RequestParam(defaultValue = "0") String value) {
        Sync sync = new Sync(id, value);
        syncRepo.save(sync);
        return "add";
    }

    @PostMapping("/text")
    public @ResponseBody
    String text(@RequestParam String original, @RequestParam String value) {
        System.err.println(original + " " + value);
        Sync sync;
        String s;
        int diff = original.length() - syncRepo.findById(5).get().getValue().length();
        if (value.charAt(0) == '-') {
            int n = Integer.parseInt(value.substring(1));
            s = original.substring(0, n - 1) + original.substring((n + 1));
        } else {
            int n = Integer.parseInt(value.substring(1, value.indexOf(',')));
            s = original.substring(0, n) + value.substring(value.indexOf(',') + 1) + ((original.length() > n) ? original.substring((n)) : "");
        }
        if (!s.isEmpty()) {
            sync = new Sync(5, s);
            syncRepo.save(sync);
        }
        return "text";
    }

    @RequestMapping("/add")
    public String add(Map<String, Object> model) {
        return "add";
    }
}
