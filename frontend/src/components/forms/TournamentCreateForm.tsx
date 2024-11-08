import { Input } from "../ui/input";
import { Label } from "../ui/label";
import { signIn } from "next-auth/react";
import React, { useState } from "react";
import { Button } from "@/components/ui/button";
import { zodResolver } from "@hookform/resolvers/zod";
import { useForm } from "react-hook-form";
import { z } from "zod";
import {
  Form,
  FormControl,
  FormField,
  FormItem,
  FormLabel,
  FormMessage,
} from "@/components/ui/form";
import { useToast } from "@/hooks/use-toast";
import { useRouter } from "next/navigation";
import { ToastAction } from "../ui/toast";
import axiosInstance from "@/lib/axios";

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
  minEloRating: z.number().min(1, {
    message: "Please input your minimum elo rating.",
  }),
  maxEloRating: z.number().min(1, {
    message: "Please input your maximum elo rating.",
  }),
  format: z.enum(["SWISS", "DOUBLE_ELIMINATION", "HYBRID"], {
    message:
      "Please input a valid format: SWISS, DOUBLE_ELIMINATION, or HYBRID.",
  }),
});

const TournamentCreateForm = () => {
  const router = useRouter();
  const { toast } = useToast();
  const [loading, setLoading] = useState(false);

  const form = useForm<z.infer<typeof formSchema>>({
    resolver: zodResolver(formSchema),
    defaultValues: {
      name: "",
      startDate: "",
      location: "",
      minEloRating: 600,
      maxEloRating: 1200,
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
      console.log(result);
    } catch (err) {
      toast({
        variant: "destructive",
        title: "Uh oh! Something went wrong.",
        description: "Please check your connection.",
        action: <ToastAction altText="Try again">Try again</ToastAction>,
      });
    }
  };

  return (
    <Form {...form}>
      <form onSubmit={form.handleSubmit(onSubmit)}>
        <div className="flex flex-col gap-6 ">
          <FormField
            control={form.control}
            name="name"
            render={({ field }) => (
              <FormItem className="grid grid-cols-4 items-center gap-4">
                <FormLabel className="text-right">Tournament Name</FormLabel>
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
                <FormLabel className="text-right">Minimum Elo Rating</FormLabel>
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
                <FormLabel className="text-right">Max Elo Rating</FormLabel>
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
                  <Input placeholder="" {...field} />
                </FormControl>
                <FormMessage className="col-span-3 col-start-2" />
              </FormItem>
            )}
          />
        </div>

        <Button
          type="submit"
          disabled={loading}
          className="hover:bg-lamaSky hover:text-gray-600 mt-5"
        >
          {loading ? "Adding Tournament..." : "Add Tournament"}
        </Button>
      </form>
    </Form>
  );
};

export default TournamentCreateForm;
