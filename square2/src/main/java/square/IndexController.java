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
    public String index(Model model) throws InterruptedException {
        /*Thread thread = new Thread(new Runnable() {
            private HashMap<Integer, String> userTexts = new HashMap<>();
            private Boolean first;

            @Override
            public void run() {
                first = true;
                //checkChanges();
            }

            private void checkChanges() {
                String activeUsers = syncRepo.findById(7).get().getValue();
                ArrayList<Integer> users = new ArrayList<>();
                while (activeUsers.contains(",") && !activeUsers.equals(",")) {
                    users.add(Integer.parseInt(activeUsers.substring(0, activeUsers.indexOf(","))));
                    activeUsers = activeUsers.substring(activeUsers.indexOf(",") + 1);
                }
                ArrayList<String> changedStrings = new ArrayList<>();
                for (int i : users) {
                    if (first || !userTexts.containsKey(i)) {
                        if (syncRepo.findById(i).isPresent())
                            userTexts.put(i, syncRepo.findById(i).get().getValue());
                    }
                    else if (syncRepo.findById(i).isPresent()) {
                        if (!userTexts.get(i).equals(syncRepo.findById(i).get().getValue())) {
                            changedStrings.add(syncRepo.findById(i).get().getValue());
                            userTexts.put(i, syncRepo.findById(i).get().getValue());
                        }
                    }
                }
                first = false;
                if (!changedStrings.isEmpty()) {
                    detectChanges(changedStrings);
                    System.err.println("detected");
                }
                try {
                    Thread.sleep(500);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                Thread newThread = new Thread(this::checkChanges);
                newThread.start();
            }

            private void detectChanges(ArrayList<String> changedStrings) {
                String original = syncRepo.findById(5).get().getValue();
                for (String s : changedStrings) {
                    String result = "";
                    int i, j = 0;
                    if (s.length() <= original.length()) {
                        for (i = 0; i < s.length() && j < original.length(); i++) {
                            if (original.charAt(j) != s.charAt(i)) {
                                result += "-" + i + ",";
                                for (; j < original.length() && s.charAt(i) != original.charAt(j); j++) {}
                                result += j + "&";
                            }
                            j++;
                        }
                        if (j - i < original.length() - s.length())
                            result += "-" + j + "," + original.length() + "&";
                    }
                    j = 0;
                    if (s.length() >= original.length()) {
                        for (i = 0; i < original.length() && j < s.length(); i++) {
                            if (original.charAt(i) != s.charAt(j)) {
                                result += "+" + i + ",";
                                for (; j < s.length() && original.charAt(i) != s.charAt(j); j++) {
                                    result += s.charAt(j);
                                }
                                result += "," + j + "&";
                            }
                            j++;
                        }
                        if (j - i < s.length() - original.length())
                            result += "+" + j + "," + s.substring(i) + "," + s.length() + "&";
                    }
                    System.out.println(result);
                }
            }
        });
        //thread.start();*/
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
