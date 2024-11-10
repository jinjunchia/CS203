import { Button } from "@/components/ui/button";
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
import axiosInstance from "@/lib/axios";
import { zodResolver } from "@hookform/resolvers/zod";
import { useRouter } from "next/navigation";
import { useState } from "react";
import { useForm } from "react-hook-form";
import { z } from "zod";
import { Input } from "../ui/input";
import { ToastAction } from "../ui/toast";
import { Label } from "@/components/ui/label";
import { RadioGroup, RadioGroupItem } from "@/components/ui/radio-group";
import { Separator } from "@/components/ui/separator";

import {
  Select,
  SelectContent,
  SelectItem,
  SelectTrigger,
  SelectValue,
} from "@/components/ui/select";

import { DialogFooter } from "@/components/ui/dialog";
import { Checkbox } from "../ui/checkbox";
import { SheetHeader } from "../ui/sheet";
import { ScrollArea } from "../ui/scroll-area";

const formSchema = z
  .object({
    durationInMinutes: z.string().min(1, {
      message: "Please input duration.",
    }),

    player1Score: z.string().min(1, {
      message: "Please input score.",
    }),

    player2Score: z.string().min(1, {
      message: "Please input score.",
    }),

    punchesPlayer1: z.string().min(1, {
      message: "Please input punches.",
    }),

    punchesPlayer2: z.string().min(1, {
      message: "Please input punches.",
    }),

    dodgesPlayer1: z.string().min(1, {
      message: "Please input dodges.",
    }),

    dodgesPlayer2: z.string().min(1, {
      message: "Please input dodges.",
    }),

    koByPlayer1: z.boolean().default(false),
    koByPlayer2: z.boolean().default(false),
  })
  .refine((data) => !(data.koByPlayer1 && data.koByPlayer2), {
    message: "Only one player can be knocked out at a time.",
    path: ["koByPlayer1", "koByPlayer2"],
  });

interface MyComponentProps {
  matchId: number;
}

const MatchUpdateForm: React.FC<MyComponentProps> = ({ matchId }) => {
  const router = useRouter();
  const { toast } = useToast();
  const [loading, setLoading] = useState(false);
  const [id, setMatchId] = useState(0);

  // setMatchId(matchId);
  // console.log(id);

  const form = useForm<z.infer<typeof formSchema>>({
    resolver: zodResolver(formSchema),
    defaultValues: {},
  });

  const onSubmit = async (values: z.infer<typeof formSchema>) => {
    setLoading(true);

    try {
      const result = await axiosInstance.post(
        "/api/tournament",
        {
          id: id,
          status: "PENDING",
          durationInMinutes: values.durationInMinutes,
          player1Score: values.player1Score,
          player2Score: values.player2Score,
          // matchDate: values.matchDate,
          punchesPlayer1: values.punchesPlayer1,
          punchesPlayer2: values.punchesPlayer2,
          dodgesPlayer1: values.dodgesPlayer1,
          dodgesPlayer2: values.dodgesPlayer2,
          koByPlayer1: values.koByPlayer1,
          koByPlayer2: values.koByPlayer2,
        },
        { withCredentials: true }
      );
    } catch (err) {
      toast({
        variant: "destructive",
        title: "Uh oh! Something went wrong.",
        description: "Please check your input!",
        action: <ToastAction altText="Try again">Try again</ToastAction>,
      });
    } finally {
      setLoading(false);
    }
  };

  return (
    <div className="grid gap-4 py-4">
      <ScrollArea className="h-5/6 w-full">
        <Form {...form}>
          <form onSubmit={form.handleSubmit(onSubmit)}>
            <div className="flex flex-col gap-6 ">
              <FormField
                control={form.control}
                name="durationInMinutes"
                render={({ field }) => (
                  <FormItem className="grid grid-cols-4 items-center gap-4">
                    <FormLabel className="text-right col-span-2">
                      Duration In Minutes
                    </FormLabel>
                    <FormControl className="col-span-2">
                      <Input placeholder="" {...field} />
                    </FormControl>
                    <FormMessage className="col-span-3 col-start-2" />
                  </FormItem>
                )}
              />

              <Separator />

              <FormField
                control={form.control}
                name="player1Score"
                render={({ field }) => (
                  <FormItem className="grid grid-cols-4 items-center gap-4">
                    <FormLabel className="text-right col-span-2">
                      Player 1 Score
                    </FormLabel>
                    <FormControl className="col-span-2">
                      <Input placeholder="" {...field} />
                    </FormControl>
                    <FormMessage className="col-span-3 col-start-2" />
                  </FormItem>
                )}
              />
              <FormField
                control={form.control}
                name="punchesPlayer1"
                render={({ field }) => (
                  <FormItem className="grid grid-cols-4 items-center gap-4">
                    <FormLabel className="text-right col-span-2">
                      Number of Punches By Player 1
                    </FormLabel>
                    <FormControl className="col-span-2">
                      <Input placeholder="" {...field} />
                    </FormControl>
                    <FormMessage className="col-span-3 col-start-2" />
                  </FormItem>
                )}
              />
              <FormField
                control={form.control}
                name="dodgesPlayer1"
                render={({ field }) => (
                  <FormItem className="grid grid-cols-4 items-center gap-4">
                    <FormLabel className="text-right col-span-2">
                      Number of Dodges By Player 1
                    </FormLabel>
                    <FormControl className="col-span-2">
                      <Input placeholder="" {...field} />
                    </FormControl>
                    <FormMessage className="col-span-3 col-start-2" />
                  </FormItem>
                )}
              />

              <Separator />

              <FormField
                control={form.control}
                name="player2Score"
                render={({ field }) => (
                  <FormItem className="grid grid-cols-4 items-center gap-4">
                    <FormLabel className="text-right col-span-2">
                      Player 2 Score
                    </FormLabel>
                    <FormControl className="col-span-2">
                      <Input placeholder="" {...field} />
                    </FormControl>
                    <FormMessage className="col-span-3 col-start-2" />
                  </FormItem>
                )}
              />

              <FormField
                control={form.control}
                name="punchesPlayer2"
                render={({ field }) => (
                  <FormItem className="grid grid-cols-4 items-center gap-4">
                    <FormLabel className="text-right col-span-2">
                      Number of Punches By Player 2
                    </FormLabel>
                    <FormControl className="col-span-2">
                      <Input placeholder="" {...field} />
                    </FormControl>
                    <FormMessage className="col-span-3 col-start-2" />
                  </FormItem>
                )}
              />
              <FormField
                control={form.control}
                name="dodgesPlayer2"
                render={({ field }) => (
                  <FormItem className="grid grid-cols-4 items-center gap-4">
                    <FormLabel className="text-right col-span-2">
                      Number of Dodges By Player 2
                    </FormLabel>
                    <FormControl className="col-span-2">
                      <Input placeholder="" {...field} />
                    </FormControl>
                    <FormMessage className="col-span-3 col-start-2" />
                  </FormItem>
                )}
              />
              <FormField
                control={form.control}
                name="koByPlayer2"
                render={({ field }) => (
                  <FormItem className="space-y-3">
                    <FormControl>
                      <RadioGroup
                        onValueChange={field.onChange}
                        className="flex flex-col space-y-1"
                      >
                        <FormItem className="flex items-center space-x-3 space-y-0">
                          <FormControl>
                            <RadioGroupItem value="all" />
                          </FormControl>
                          <FormLabel className="font-normal">
                            Player 1 was KOed
                          </FormLabel>
                        </FormItem>
                        <FormItem className="flex items-center space-x-3 space-y-0">
                          <FormControl>
                            <RadioGroupItem value="mentions" />
                          </FormControl>
                          <FormLabel className="font-normal">
                            Player 2 was KOed
                          </FormLabel>
                        </FormItem>
                        <FormItem className="flex items-center space-x-3 space-y-0">
                          <FormControl>
                            <RadioGroupItem value="none" />
                          </FormControl>
                          <FormLabel className="font-normal">No KOs</FormLabel>
                        </FormItem>
                      </RadioGroup>
                    </FormControl>
                  </FormItem>
                )}
              />
            </div>
            <Button
              type="submit"
              disabled={loading}
              className="hover:bg-lamaSky hover:text-gray-600 mt-5"
            >
              {loading ? "Updating..." : "Update"}
            </Button>
          </form>
        </Form>
      </ScrollArea>
    </div>
  );
};

export default MatchUpdateForm;
