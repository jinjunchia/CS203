import { Button } from "@/components/ui/button";
import {
  Form,
  FormControl,
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

import {
  Select,
  SelectContent,
  SelectItem,
  SelectTrigger,
  SelectValue,
} from "@/components/ui/select";

import {
  Dialog,
  DialogClose,
  DialogContent,
  DialogDescription,
  DialogFooter,
  DialogHeader,
  DialogTitle,
  DialogTrigger,
} from "@/components/ui/dialog";

// import * as dialog from "@/components/ui/dialog";
import Image from "next/image";

const formSchema = z.object({
  name: z.string().min(1, {
    message: "Please input your Tournament Name.",
  }),
  startDate: z.string().min(1, {
    message: "Please input your start date.",
  }),
  location: z.string().min(1, {
    message: "Please input your location.",
  }),
  minEloRating: z.string().min(1, {
    message: "Please input your minimum elo rating.",
  }),
  maxEloRating: z.string().min(1, {
    message: "Please input your maximum elo rating.",
  }),
  format: z.enum(["SWISS", "DOUBLE_ELIMINATION", "HYBRID"], {
    message:
      "Please input a valid format: SWISS, DOUBLE_ELIMINATION, or HYBRID.",
  }),
});

interface MyComponentProps {
  onRefresh: () => void; // Add this prop to type definition
}

const TournamentCreateForm: React.FC<MyComponentProps> = ({ onRefresh }) => {
  const router = useRouter();
  const { toast } = useToast();
  const [dialogOpen, setDialogOpen] = useState(false);
  const [loading, setLoading] = useState(false);

  const form = useForm<z.infer<typeof formSchema>>({
    resolver: zodResolver(formSchema),
    defaultValues: {
      name: "",
      startDate: "",
      location: "",
      minEloRating: "600",
      maxEloRating: "1200",
      format: "SWISS",
    },
  });

  const onSubmit = async (values: z.infer<typeof formSchema>) => {
    setLoading(true);

    try {
      const result = await axiosInstance.post(
        "/api/tournament",
        {
          name: values.name,
          startDate: values.startDate,
          location: values.location,
          minEloRating: values.minEloRating,
          maxEloRating: values.maxEloRating,
          format: values.format,
        },
        { withCredentials: true }
      );
      onRefresh();
      router.refresh();
      setDialogOpen(false);
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
    <Dialog open={dialogOpen} onOpenChange={setDialogOpen}>
      <DialogTrigger asChild>
        <button
          onClick={() => setDialogOpen(true)}
          className="w-8 h-8 flex items-center justify-center rounded-full bg-lamaYellow"
        >
          <Image src="/plus.png" alt="" width={14} height={14} />
        </button>
      </DialogTrigger>
      <DialogContent className="sm:max-w-[640px]">
        <DialogHeader>
          <DialogTitle>Create Tournament</DialogTitle>
          <DialogDescription>The journey starts now!</DialogDescription>
        </DialogHeader>
        <div className="grid gap-4 py-4">
          <Form {...form}>
            <form onSubmit={form.handleSubmit(onSubmit)}>
              <div className="flex flex-col gap-6 ">
                <FormField
                  control={form.control}
                  name="name"
                  render={({ field }) => (
                    <FormItem className="grid grid-cols-4 items-center gap-4">
                      <FormLabel className="text-right">
                        Tournament Name
                      </FormLabel>
                      <FormControl className="col-span-3">
                        <Input placeholder="" {...field} />
                      </FormControl>
                      <FormMessage className="col-span-3 col-start-2" />
                    </FormItem>
                  )}
                />
                <FormField
                  control={form.control}
                  name="startDate"
                  render={({ field }) => (
                    <FormItem className="grid grid-cols-4 items-center gap-4">
                      <FormLabel className="text-right">Start Date</FormLabel>
                      <FormControl className="col-span-3">
                        <Input placeholder="" {...field} />
                      </FormControl>
                      <FormMessage className="col-span-3 col-start-2" />
                    </FormItem>
                  )}
                />
                <FormField
                  control={form.control}
                  name="location"
                  render={({ field }) => (
                    <FormItem className="grid grid-cols-4 items-center gap-4">
                      <FormLabel className="text-right">Location</FormLabel>
                      <FormControl className="col-span-3">
                        <Input placeholder="" {...field} />
                      </FormControl>
                      <FormMessage className="col-span-3 col-start-2" />
                    </FormItem>
                  )}
                />
                <FormField
                  control={form.control}
                  name="minEloRating"
                  render={({ field }) => (
                    <FormItem className="grid grid-cols-4 items-center gap-4">
                      <FormLabel className="text-right">
                        Minimum Elo Rating
                      </FormLabel>
                      <FormControl className="col-span-3">
                        <Input placeholder="" {...field} />
                      </FormControl>
                      <FormMessage className="col-span-3 col-start-2" />
                    </FormItem>
                  )}
                />
                <FormField
                  control={form.control}
                  name="maxEloRating"
                  render={({ field }) => (
                    <FormItem className="grid grid-cols-4 items-center gap-4">
                      <FormLabel className="text-right">
                        Maximum Elo Rating
                      </FormLabel>
                      <FormControl className="col-span-3">
                        <Input placeholder="" {...field} />
                      </FormControl>
                      <FormMessage className="col-span-3 col-start-2" />
                    </FormItem>
                  )}
                />
                <FormField
                  control={form.control}
                  name="format"
                  render={({ field }) => (
                    <FormItem className="grid grid-cols-4 items-center gap-4">
                      <FormLabel className="text-right">Format</FormLabel>
                      <FormControl className="col-span-3">
                        <Select
                          onValueChange={field.onChange}
                          defaultValue={field.value}
                        >
                          <SelectTrigger className="w-[180px]">
                            <SelectValue placeholder="Select Format" />
                          </SelectTrigger>
                          <SelectContent>
                            <SelectItem value="SWISS">Swiss</SelectItem>
                            <SelectItem value="DOUBLE_ELIMINATION">
                              Double Elimination
                            </SelectItem>
                            <SelectItem value="HYBRID">Hybrid</SelectItem>
                          </SelectContent>
                        </Select>
                      </FormControl>
                      <FormMessage className="col-span-3 col-start-2" />
                    </FormItem>
                  )}
                />
              </div>

              <DialogFooter className="sm:justify-start">
                {/* <DialogClose asChild> */}
                <Button
                  type="submit"
                  disabled={loading}
                  className="hover:bg-lamaSky hover:text-gray-600 mt-5"
                >
                  {loading ? "Adding Tournament..." : "Add Tournament"}
                </Button>
                {/* </DialogClose> */}
              </DialogFooter>
            </form>
          </Form>
        </div>
      </DialogContent>
    </Dialog>
  );
};

export default TournamentCreateForm;
