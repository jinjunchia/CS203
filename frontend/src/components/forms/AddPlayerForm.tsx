import {
  Form,
  FormControl,
  FormDescription,
  FormField,
  FormItem,
  FormLabel,
  FormMessage,
} from "@/components/ui/form";
import { useToast } from "@/hooks/use-toast";
import { zodResolver } from "@hookform/resolvers/zod";
import { useRouter } from "next/navigation";
import { useEffect, useState } from "react";
import { useForm } from "react-hook-form";
import { z } from "zod";
import { Button } from "../ui/button";
import { Checkbox } from "../ui/checkbox";
import axiosInstance from "@/lib/axios";
import { ScrollArea } from "../ui/scroll-area";
import { SheetClose } from "../ui/sheet";

const FormSchema = z.object({
  playerIds: z.array(z.number()).refine((value) => value.some((item) => item), {
    message: "You have to select at least one item.",
  }),
});

interface MyComponentProps {
  currentPlayers: any[];
  tournamentId: any;
  onRefresh: () => void;
}

const AddPlayerForm: React.FC<MyComponentProps> = ({
  currentPlayers,
  tournamentId,
  onRefresh,
}) => {
  const router = useRouter();
  const { toast } = useToast();
  const [loading, setLoading] = useState(false);
  const [players, setPlayers] = useState<any[]>([]); // Ensure players starts as an array

  useEffect(() => {
    const fetchPlayers = async () => {
      try {
        const response = await axiosInstance.get("/api/player");
        // Ensure players is always an array
        setPlayers(Array.isArray(response.data) ? response.data : []);
      } catch (err) {
        console.error("Error fetching players:", err);
        setLoading(false);
      }
    };

    fetchPlayers();
  }, []);

  const form = useForm<z.infer<typeof FormSchema>>({
    resolver: zodResolver(FormSchema),
    defaultValues: {
      playerIds: [],
    },
  });

  async function onSubmit(data: z.infer<typeof FormSchema>) {
    // toast({
    //   title: "You submitted the following values:",
    //   description: (
    //     <pre className="mt-2 w-[340px] rounded-md bg-slate-950 p-4">
    //       <code className="text-white">{JSON.stringify(data, null, 2)}</code>
    //     </pre>
    //   ),
    // });

    try {
      const response = await axiosInstance.put(
        `/api/tournament/${tournamentId}/players`,
        JSON.stringify(data, null, 2),
        { withCredentials: true }
      );

      // Ensure players stays an array
      setPlayers(Array.isArray(response.data) ? response.data : []);

      onRefresh();
      // Refresh the page to display the updated data
      router.refresh();
    } catch (err) {
      console.error("Error updating players:", err);
    }
  }

  return (
    <div className="grid gap-4 py-4">
      <ScrollArea className="h-5/6 w-full">
        <Form {...form}>
          <form onSubmit={form.handleSubmit(onSubmit)} className="space-y-8">
            <FormField
              control={form.control}
              name="playerIds"
              render={() => (
                <FormItem>
                  {players.map((item) => (
                    <FormField
                      key={item.id}
                      control={form.control}
                      name="playerIds"
                      render={({ field }) => (
                        <FormItem
                          key={item.id}
                          className="flex flex-row items-start space-x-3 space-y-0"
                        >
                          <FormControl>
                            <Checkbox
                              checked={field.value?.includes(item.id)}
                              onCheckedChange={(checked) =>
                                checked
                                  ? field.onChange([...field.value, item.id])
                                  : field.onChange(
                                      field.value?.filter(
                                        (value) => value !== item.id
                                      )
                                    )
                              }
                            />
                          </FormControl>
                          <FormLabel className="font-normal">
                            {item.username}
                          </FormLabel>
                        </FormItem>
                      )}
                    />
                  ))}
                  <FormMessage />
                </FormItem>
              )}
            />
            <SheetClose>
              <Button type="submit">Submit</Button>
            </SheetClose>
          </form>
        </Form>
      </ScrollArea>
    </div>
  );
};

export default AddPlayerForm;
